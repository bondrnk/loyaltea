package loyaltea
package repo

import model.*

import zio.*

trait UserRepo {
  def create(user: User): Task[User]
  def list(): Task[List[User]]
}

object UserRepo {
  case class Dummy()    extends UserRepo {
    private val persistTo: DummyRepo.PersistTo     = Some(os.pwd / ".loyaltea" / "json" / "user.json")
    private val repo                               = DummyRepo.Json[UserId, User](_.id, persistTo)
    def list(): Task[List[User]]               = repo.list
    def create(user: User): Task[User] = repo.create(user)
  }
  
  case class Postgres() extends UserRepo {
    def list(): Task[List[User]]               = ZIO.succeed(Nil)
    def create(user: User): Task[User] = ZIO.succeed(user)
  }
}
