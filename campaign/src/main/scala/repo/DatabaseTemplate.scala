package loyaltea
package repo

import doobie.*
import io.getquill.*
import io.getquill.doobie.DoobieContext
import zio.*
import zio.interop.catz.*

abstract class DatabaseContext                            extends DoobieContext.Postgres(SnakeCase)
case class DatabaseTemplate(transactor: Transactor[Task]) extends DatabaseContext {
  implicit class TransactOps[A](ma: ConnectionIO[A]) {
    def transact: Task[A] = transactor.trans.apply(ma)
  }
}
