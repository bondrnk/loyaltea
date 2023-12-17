package loyaltea
package api

import model.*
import repo.UserRepo

import io.scalaland.chimney.dsl.*
import smithy4s.user.*
import zio.*

import java.util.UUID

final class UserApi(service: UserService[Task]) extends HttpApi(service)

final class UserServiceImpl(repo: UserRepo) extends UserService[Task] {
  override def create(dto: CreateUserRequest): Task[UserDTO] = {
    val user = dto.into[User].enableDefaultValues.transform
    repo.create(user).map(_.transformInto[UserDTO])
  }

  override def list(): Task[UserListResponse] =
    repo.list().map(users => UserListResponse(users.map(_.transformInto[UserDTO])))
}
