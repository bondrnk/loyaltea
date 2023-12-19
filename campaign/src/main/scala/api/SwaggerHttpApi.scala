package loyaltea
package api

import smithy4s.campaign.*
import zio.*
import zio.interop.catz.*

case class SwaggerHttpApi() extends HttpApi(smithy4s.http4s.swagger.docs[Task](CampaignService))
