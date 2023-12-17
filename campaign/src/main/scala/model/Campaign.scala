package loyaltea
package model

import io.circe.Codec
import io.circe.generic.semiauto

import java.util.UUID

type CampaignId    = UUID
type FulfillmentId = UUID
case class Campaign(id: CampaignId, name: String, fulfillments: List[FulfillmentId])

object Campaign {
  implicit val codec: Codec.AsObject[Campaign] = semiauto.deriveCodec
}
