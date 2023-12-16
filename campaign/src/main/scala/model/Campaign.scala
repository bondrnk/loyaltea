package loyaltea
package model

import io.circe.Codec
import io.circe.generic.semiauto

import java.util.UUID

case class Campaign (id: UUID)

object Campaign {
  implicit val codec: Codec.AsObject[Campaign] = semiauto.deriveCodec
}
