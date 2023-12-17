package loyaltea
package repo

import model.*

import zio.*

trait CampaignRepo {
  def create(campaign: Campaign): Task[Campaign]
  def list(): Task[List[Campaign]]
}

object CampaignRepo {
  case class Dummy()    extends CampaignRepo {
    private val persistTo: DummyRepo.PersistTo     = Some(os.pwd / ".loyaltea" / "json" / "campaign.json")
    private val repo                               = DummyRepo.Json[CampaignId, Campaign](_.id, persistTo)
    def list(): Task[List[Campaign]]               = repo.list
    def create(campaign: Campaign): Task[Campaign] = repo.create(campaign)
  }
  
  case class Postgres() extends CampaignRepo {
    def list(): Task[List[Campaign]]               = ZIO.succeed(Nil)
    def create(campaign: Campaign): Task[Campaign] = ZIO.succeed(campaign)
  }
}
