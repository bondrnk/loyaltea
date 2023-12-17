package loyaltea
package api

import smithy4s.user.*
import zio.*
import zio.interop.catz.*

case class SwaggerApi() extends HttpApi(smithy4s.http4s.swagger.docs[Task](UserService))
