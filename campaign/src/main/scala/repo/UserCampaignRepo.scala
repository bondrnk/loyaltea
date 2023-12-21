package loyaltea
package repo

import model.*

import zio.*

case class UserCampaignQuery(
    campaignId: Option[CampaignId] = None,
    userId: Option[UserId] = None,
    tenant: Option[Tenant] = None,
)

object UserCampaignQuery {
  def id(campaignId: CampaignId, userId: UserId): UserCampaignQuery =
    UserCampaignQuery(campaignId = Some(campaignId), userId = Some(userId))
}

trait UserCampaignRepo {
  def create(campaign: UserCampaign): Task[UserCampaign]
  def read(query: UserCampaignQuery): Task[Option[UserCampaign]]
  def update(campaign: UserCampaign): Task[UserCampaign]
  def delete(campaignId: CampaignId, userId: UserId): Task[Unit]
  def list(query: UserCampaignQuery, page: PageQuery = PageQuery.all): Task[List[UserCampaign]]
}

object UserCampaignRepo {
  case class Dummy() extends UserCampaignRepo {
    private val persistTo: DummyRepo.PersistTo = Some(os.pwd / ".loyaltea" / "json" / "user_campaign.json")
    private val repo                           = DummyRepo.Json[UserCampaignId, UserCampaign](_.id, persistTo)
    def create(campaign: UserCampaign): Task[UserCampaign]                        = repo.create(campaign)
    def read(query: UserCampaignQuery): Task[Option[UserCampaign]]          = list(query).map(_.headOption)
    def update(campaign: UserCampaign): Task[UserCampaign]                        = repo.update(campaign)
    def delete(campaignId: CampaignId, userId: UserId): Task[Unit]                        = repo.delete((campaignId, userId))

    def list(query: UserCampaignQuery, page: PageQuery): Task[List[UserCampaign]] =
      repo.list
        .map { items =>
          items
            .filterOpt(query.campaignId)(_.campaignId == _)
            .filterOpt(query.userId)(_.userId == _)
            .filterOpt(query.tenant)(_.tenant == _)
            .takeOpt(page.size)
            .dropOpt(page.offset)
        }
  }

  case class Postgres(template: DatabaseTemplate) extends UserCampaignRepo {

    import io.getquill.*
    import io.getquill.extras.*
    import template.*

    private inline def campaigns = quote(querySchema[UserCampaign]("user_campaigns"))

    def create(campaign: UserCampaign): Task[UserCampaign] =
      run(campaigns.insertValue(lift(campaign)).onConflictIgnore).transact.as(campaign)

    def read(query: UserCampaignQuery): Task[Option[UserCampaign]] =
      list(query, PageQuery.one).map(_.headOption)
    def update(campaign: UserCampaign): Task[UserCampaign]               =
      run(campaigns.updateValue(lift(campaign))).transact.as(campaign)

    def delete(campaignId: CampaignId, userId: UserId): Task[Unit] =
      run(campaigns.filter(_.userId == lift(userId)).filter(_.campaignId == lift(campaignId)).delete).transact.unit

    def list(query: UserCampaignQuery, page: PageQuery): Task[List[UserCampaign]] = run {
      campaigns.dynamic
        .filterOpt(query.campaignId)((e, id) => quote(e.campaignId === unquote(id)))
        .filterOpt(query.userId)((e, id) => quote(e.userId === unquote(id)))
        .filterOpt(query.tenant)((e, tenant) => quote(e.tenant === unquote(tenant)))
        .takeOpt(page.size)
        .dropOpt(page.offset)
    }.transact
  }
}
