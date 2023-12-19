package loyaltea
package model

import io.circe.Codec
import io.circe.generic.semiauto

import java.util.UUID

type UserId = UUID
type UserCampaignId = (CampaignId, UserId)

case class UserCampaign(
    tenant: Tenant,
    campaignId: CampaignId,
    userId: UserId,
    fulfillments: List[FulfillmentId],
) {
  def id: UserCampaignId = (campaignId, userId)
}

object UserCampaign {
  implicit val codec: Codec.AsObject[UserCampaign] = semiauto.deriveCodec
}
