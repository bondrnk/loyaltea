package loyaltea
package api

import model.*
import repo.{CampaignQuery, CampaignRepo}

import io.scalaland.chimney.dsl.*
import smithy4s.campaign.*
import zio.*

import java.util.UUID

final class CampaignHttpApi(service: CampaignService[Task]) extends HttpApi(service)

final class CampaignServiceImpl(repo: CampaignRepo) extends CampaignService[Task] {
  override def create(dto: CreateCampaignRequest): Task[CampaignDTO] = {
    val campaign = dto.into[Campaign].enableDefaultValues.transform
    repo.create(campaign).map(_.transformInto[CampaignDTO])
  }

  override def list(): Task[CampaignListResponse] =
    repo.list(CampaignQuery()).map(campaigns => CampaignListResponse(campaigns.map(_.transformInto[CampaignDTO])))
}
