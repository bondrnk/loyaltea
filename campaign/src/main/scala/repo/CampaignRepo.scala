package loyaltea
package repo

import model.*

import zio.*

case class CampaignQuery(
    campaignId: Option[CampaignId] = None,
    fulfillmentId: Option[FulfillmentId] = None,
)

trait CampaignRepo {
  def create(campaign: Campaign): Task[Campaign]
  def read(query: CampaignQuery): Task[Option[Campaign]]
  def list(query: CampaignQuery, page: PageQuery = PageQuery.all): Task[List[Campaign]]
}

object CampaignRepo {
  case class Dummy()    extends CampaignRepo {
    private val persistTo: DummyRepo.PersistTo     = Some(os.pwd / ".loyaltea" / "json" / "campaign.json")
    private val repo                               = DummyRepo.Json[CampaignId, Campaign](_.id, persistTo)
    def create(campaign: Campaign): Task[Campaign] = repo.create(campaign)
    def read(query: CampaignQuery): Task[Option[Campaign]] = list(query).map(_.headOption)

    def list(query: CampaignQuery, page: PageQuery): Task[List[Campaign]] =
      repo.list
        .map { items =>
          items
            .filterOpt(query.campaignId)(_.id == _)
            .filterOpt(query.fulfillmentId)(_.fulfillments contains _)
            .takeOpt(page.size)
            .dropOpt(page.offset)
        }
  }
  
  case class Postgres(template: DatabaseTemplate) extends CampaignRepo {
    import template.*
    import io.getquill.*
    import io.getquill.extras._
    inline def campaigns = quote(querySchema[Campaign]("campaigns"))
    def create(campaign: Campaign): Task[Campaign] =
      run(campaigns.insertValue(lift(campaign)).onConflictIgnore).transact.as(campaign)

    def read(query: CampaignQuery): Task[Option[Campaign]] =
      list(query, PageQuery.one).map(_.headOption)

    def list(query: CampaignQuery, page: PageQuery): Task[List[Campaign]]               = run {
      campaigns.dynamic
        .filterOpt(query.campaignId)((e, id) => quote(e.id === unquote(id)))
        .filterOpt(query.fulfillmentId)((e, fulfillmentId) => quote(e.fulfillments.contains(fulfillmentId)))
        .takeOpt(page.size)
        .dropOpt(page.offset)
    }.transact
  }
}
