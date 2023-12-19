package loyaltea
package repo

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class PageQuery(size: Option[Int] = None, page: Option[Int] = None) {
  def offset: Option[Int] = size.flatMap(s => page.map(p => p * s))
}

object PageQuery {
  val all: PageQuery                   = PageQuery(size = None, page = None)
  val one: PageQuery                   = PageQuery(size = Some(1), page = None)
  implicit val codec: Codec[PageQuery] = deriveCodec
}