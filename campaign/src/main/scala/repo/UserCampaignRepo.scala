package loyaltea
package repo

import model.*

import zio.*

trait UserCampaignRepo {
  def create(campaign: UserCampaign): Task[UserCampaign]
  def read(userCampaignId: UserCampaignId): Task[Option[UserCampaign]]
  def update(campaign: UserCampaign): Task[UserCampaign]
  def delete(userCampaignId: UserCampaignId): Task[Unit]
  def list(): Task[List[UserCampaign]]
}

object UserCampaignRepo {
  case class Dummy()    extends UserCampaignRepo {
    private val persistTo: DummyRepo.PersistTo     = Some(os.pwd / ".loyaltea" / "json" / "user-campaign.json")
    private val repo                               = DummyRepo.Json[UserCampaignId, UserCampaign](_.id, persistTo)
    def list(): Task[List[UserCampaign]]               = repo.list
    def create(campaign: UserCampaign): Task[UserCampaign] = repo.create(campaign)
    def read(userCampaignId: UserCampaignId): Task[Option[UserCampaign]] = repo.read(userCampaignId)
    def update(campaign: UserCampaign): Task[UserCampaign] = repo.update(campaign)
    def delete(userCampaignId: UserCampaignId): Task[Unit] = repo.delete(userCampaignId)
  }
  
  case class Postgres() extends UserCampaignRepo {
    def list(): Task[List[UserCampaign]]               = ZIO.succeed(Nil)
    def create(campaign: UserCampaign): Task[UserCampaign] = ZIO.succeed(campaign)
    def read(userCampaignId: UserCampaignId): Task[Option[UserCampaign]] = ZIO.none
    def update(campaign: UserCampaign): Task[UserCampaign] = ZIO.succeed(campaign)

    def delete(userCampaignId: UserCampaignId): Task[Unit] = ZIO.unit

  }
}
