package loyaltea
package http

import api.HttpApi

import cats.implicits.*
import com.comcast.ip4s.Port
import fs2.io.net.Network
import izumi.distage.model.definition.Lifecycle
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import zio.*
import zio.interop.catz.*

final case class HttpServer(
    server: Server
)

object HttpServer {
  final class Impl(
      allHttpApis: Set[HttpApi]
  ) extends Lifecycle.Of[Task, HttpServer](
        Lifecycle.fromCats {
          val combinedApis = allHttpApis.map(_.http).toList.foldK

          EmberServerBuilder
            .default[Task](asyncInstance, Network.forAsync)
            .withHttpApp(combinedApis.orNotFound)
            .withPort(Port.fromInt(8080).get)
            .withIdleTimeout(Duration.Infinity.asScala)
            .build
            .map(HttpServer(_))
        }
      )
}
