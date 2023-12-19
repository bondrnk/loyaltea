package loyaltea

import model.RewardEvent

import loyaltea.error.*
import loyaltea.repo.*
import loyaltea.model.*
import loyaltea.producers.Producers
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
        userCampaignId = (campaign.id, event.userId)
        userCampaignFound <- userCampaignRepo.read(userCampaignId)
        updatedUserCampaign <- userCampaignFound match
          case Some(userCampaign) => userCampaignRepo.update(userCampaign.copy(fulfillments = userCampaign.fulfillments :+ event.fulfillmentId))
          case None => userCampaignRepo.create(UserCampaign(event.tenant, campaign.id, event.userId, List(event.fulfillmentId)))
        _ <- ZIO.when(campaign.isCompleted(updatedUserCampaign)) {
          ZIO.foreachDiscard(campaign.rewards)(rewardId => producers.produceReward(RewardEvent(event.tenant, rewardId, event.userId))) *> userCampaignRepo.delete(userCampaignId)
        }
      } yield ()
  }
}
