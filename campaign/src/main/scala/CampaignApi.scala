package loyaltea

import error.*
import model.*
import producers.Producers
import repo.*

import zio.*

trait CampaignApi {
  def processFulfillment(event: FulfillmentEvent): Task[Unit]
}

object CampaignApi {
  final class Live(
    campaignRepo: CampaignRepo,
    userCampaignRepo: UserCampaignRepo,
    producers: Producers,  
  ) extends CampaignApi {
    override def processFulfillment(event: FulfillmentEvent): Task[Unit] =
      for {
        campaign <- campaignRepo.read(CampaignQuery(fulfillmentId = Some(event.fulfillmentId))).someOrFail(error"campaign.not.found")
        _ <- ZIO.when(!campaign.fulfillments.contains(event.fulfillmentId))(ZIO.fail(error"campaign.fulfillment.not.found"))
        userCampaignFound <- userCampaignRepo.read(UserCampaignQuery.id(campaign.id, event.userId))
        updatedUserCampaign <- userCampaignFound match
          case Some(userCampaign) => userCampaignRepo.update(userCampaign.copy(fulfillments = userCampaign.fulfillments :+ event.fulfillmentId))
          case None => userCampaignRepo.create(UserCampaign(event.tenant, campaign.id, event.userId, List(event.fulfillmentId)))
        _ <- ZIO.when(campaign.isCompleted(updatedUserCampaign)) {
          ZIO.foreachDiscard(campaign.rewards)(rewardId => producers.produceReward(RewardEvent(event.tenant, rewardId, event.userId))) *> userCampaignRepo.delete(campaign.id, event.userId)
        }
      } yield ()
  }
}
