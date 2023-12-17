package loyaltea
package api

import model.*
import repo.CampaignRepo

import io.scalaland.chimney.dsl.*
import smithy4s.campaign.*
import zio.*

import java.util.UUID

final class CampaignApi(service: CampaignService[Task]) extends HttpApi(service)

final class CampaignServiceImpl(repo: CampaignRepo) extends CampaignService[Task] {
  override def create(name: String, fulfillments: List[UUID]): Task[CampaignDTO] =
    repo.create(Campaign(UUID.randomUUID(), name, fulfillments)).map(_.transformInto[CampaignDTO])

  override def list(): Task[CampaignListResponse] =
    repo.list().map(campaigns => CampaignListResponse(campaigns.map(_.transformInto[CampaignDTO])))
}
