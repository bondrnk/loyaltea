package loyaltea
package model

import io.circe.Codec
import io.circe.generic.semiauto

import java.util.UUID

type UserId = UUID
object UserId {
  def gen: UserId = UUID.randomUUID()
}

case class User(id: UserId = UserId.gen, fullname: String, username: String)

object User {
  implicit val codec: Codec.AsObject[User] = semiauto.deriveCodec
}
