package loyaltea
package http

import cats.effect.Async
import cats.implicits.*
import com.comcast.ip4s.Port
import fs2.io.net.Network
import izumi.distage.model.definition.Lifecycle
import api.HttpApi
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import zio.*
final case class HttpServer(
  server: Server
)

object HttpServer {
  final class Impl(
    allHttpApis: Set[HttpApi]
  )(implicit
    async: Async[Task]
  ) extends Lifecycle.Of[Task, HttpServer](
      Lifecycle.fromCats {
        val combinedApis = allHttpApis.map(_.http).toList.foldK

        EmberServerBuilder
          .default[Task](async)
          .withHttpApp(combinedApis.orNotFound)
          .withPort(Port.fromInt(8080).get)
          .build
          .map(HttpServer(_))
      }
    )

}
