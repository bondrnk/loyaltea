package loyaltea
package model

import io.circe.Codec
import io.circe.generic.semiauto

case class FulfillmentEvent(
    tenant: Tenant,
    fulfillmentId: FulfillmentId,
    userId: UserId,
)

object FulfillmentEvent {
  implicit val codec: Codec.AsObject[FulfillmentEvent] = semiauto.deriveCodec
}
