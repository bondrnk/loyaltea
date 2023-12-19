package loyaltea
package model

import io.circe.Codec
import io.circe.generic.semiauto

import java.util.UUID

type CampaignId    = UUID
type Tenant        = String
type FulfillmentId = UUID
type RewardId = UUID

case class Campaign(
    id: CampaignId = UUID.randomUUID(),
    tenant: Tenant,
    name: String,
    fulfillments: List[FulfillmentId],
    rewards: List[RewardId]
) {
  def isCompleted(user: UserCampaign): Boolean = fulfillments.toSet == user.fulfillments.toSet
}

object Campaign {
  implicit val codec: Codec.AsObject[Campaign] = semiauto.deriveCodec
}
