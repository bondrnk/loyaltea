package loyaltea
package api

import io.circe.syntax.*
import izumi.functional.bio.IO2
import izumi.functional.bio.catz.*
import org.http4s.HttpRoutes
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import zio.*

//final class CampaignApiImpl() extends CampaignService

final class CampaignApi(
  dsl: Http4sDsl[Task],
) extends HttpApi {

  import dsl.*

  override def http: HttpRoutes[Task] = {
    HttpRoutes.of {
      case GET -> Root / "campaign" =>
        Ok()
    }
  }
}
