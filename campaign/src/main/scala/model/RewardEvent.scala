package loyaltea
package model

import io.circe.Codec
import io.circe.generic.semiauto

case class RewardEvent(
    tenant: Tenant,
    rewardId: CampaignId,
    userId: UserId,
) {
  def key: String = s"$rewardId/$userId"
}

object RewardEvent {
  implicit val codec: Codec.AsObject[RewardEvent] = semiauto.deriveCodec
}
