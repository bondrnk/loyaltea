package loyaltea
package api

import org.http4s.HttpRoutes
import zio.Task

trait HttpApi {
  def http: HttpRoutes[Task]
}
