package loyaltea
package api

import scala.language.implicitConversions

import org.http4s.*
import smithy4s.http4s.SimpleRestJsonBuilder
import smithy4s.kinds.FunctorAlgebra
import zio.*
import zio.interop.catz.*

trait HttpApi(val http: HttpRoutes[Task])

implicit def toRoutes[Alg[_[_, _, _, _, _]]: smithy4s.Service](alg: FunctorAlgebra[Alg, Task]): HttpRoutes[Task] =
  SimpleRestJsonBuilder.routes(alg).make.toOption.get
