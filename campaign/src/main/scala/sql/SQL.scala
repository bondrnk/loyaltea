package loyaltea
package sql

import doobie.*
import izumi.functional.bio.Panic2
import izumi.functional.bio.catz.*
import zio.*
import model.*

trait SQL[F[_, _]] {
  def execute[A](queryName: String)(conn: ConnectionIO[A]): F[QueryFailure, A]
}

object SQL {
  final class Impl[F[+_, +_]: Panic2](
    transactor: Transactor[F[Throwable, _]]
  ) extends SQL[F] {
    override def execute[A](queryName: String)(conn: ConnectionIO[A]): F[QueryFailure, A] = {
      transactor.trans
        .apply(conn)
        .leftMap(QueryFailure(queryName, _))
    }
  }
}
