package loyaltea
package error

import scala.util.control.NoStackTrace

import io.circe.generic.semiauto
import io.circe.{Codec, Json}
import org.http4s.Status

def applicationError(error: String, details: Option[Json] = None): AppError =
  AppError.ApplicationError(error, details)
def serverError(error: String): AppError                                    = AppError.ServerError(error)
def unauthorizedError(error: String): AppError                              = AppError.UnauthorizedError(error)

implicit class JapErrorInterpolator(sc: StringContext) {
  def error(args: Any*): AppError = {
    AppError.ApplicationError(StringContext.standardInterpolator(identity, args, sc.parts))
  }
}
sealed abstract class AppError(message: String) extends Throwable(message) with NoStackTrace {
  def status: Status
}

object AppError {
  case class ApplicationError(message: String, details: Option[Json] = None) extends AppError(message) {
    def details(json: Json): ApplicationError = copy(details = Some(json))
    val status: Status                        = Status.BadRequest
  }

  case class ServerError(message: String) extends AppError(message) {
    val status: Status = Status.InternalServerError
  }

  case class UnauthorizedError(message: String) extends AppError(message) {
    val status: Status = Status.Unauthorized
  }
  implicit val codec: Codec.AsObject[AppError] = semiauto.deriveCodec
}
