package loyaltea
package plugins

import api.*
import config.*
import http.HttpServer
import repo.*
import scala.concurrent.duration.*
import sql.*

import distage.StandardAxis.Repo
import distage.config.ConfigModuleDef
import distage.plugins.PluginDef
import distage.{ModuleDef, Scene}
import doobie.util.transactor.Transactor
import izumi.distage.roles.bundled.BundledRolesModule
import izumi.distage.roles.model.definition.RoleModuleDef
import izumi.fundamentals.platform.integration.PortCheck
import smithy4s.user.*
import zio.*

object UserPlugin extends PluginDef {
  include(modules.roles)
  include(modules.api)
  include(modules.repoDummy)
  include(modules.repoProd)
  include(modules.configs)
  include(modules.prodConfigs)

  object modules {
    def roles: RoleModuleDef = new RoleModuleDef {
      // The `user` role
      makeRole[UserRole]
      // Add bundled roles: `help` & `configwriter`
      include(BundledRolesModule[Task](version = "1.0.0"))
    }

    def api: ModuleDef = new ModuleDef {
      // The `user` API
      make[UserApi]
      make[SwaggerApi]
      make[UserService[Task]].from[UserServiceImpl]

      many[HttpApi]
        .add[UserApi]
        .add[SwaggerApi]

      make[HttpServer].fromResource[HttpServer.Impl]
    }

    def repoDummy: ModuleDef = new ModuleDef {
      tag(Repo.Dummy)

      make[UserRepo].from[UserRepo.Dummy]
    }

    def repoProd: ModuleDef = new ModuleDef {
      tag(Repo.Prod)

      make[UserRepo].from[UserRepo.Postgres]
      make[Transactor[Task]].fromResource[TransactorResource[Task]]
      make[PortCheck].from(new PortCheck(3.seconds))
    }

    val configs: ConfigModuleDef     = new ConfigModuleDef {
      makeConfig[PostgresCfg]("postgres")
    }
    val prodConfigs: ConfigModuleDef = new ConfigModuleDef {
      // only use this if Scene axis is set to Provided
      tag(Scene.Provided)

      makeConfig[PostgresPortCfg]("postgres")
    }
  }
}
