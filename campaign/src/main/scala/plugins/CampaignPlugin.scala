package loyaltea
package plugins

import api.*
import config.*
import http.HttpServer
import repo.*
import sql.*

import distage.StandardAxis.Repo
import distage.config.ConfigModuleDef
import distage.plugins.PluginDef
import distage.{ModuleDef, Scene, TagKK}
import doobie.util.transactor.Transactor
import izumi.distage.roles.bundled.BundledRolesModule
import izumi.distage.roles.model.definition.RoleModuleDef
import izumi.fundamentals.platform.integration.PortCheck
import org.http4s.dsl.Http4sDsl
import zio.*

import scala.concurrent.duration.*

object CampaignPlugin extends PluginDef {
  include(modules.roles)
  include(modules.api)
  include(modules.repoDummy)
  include(modules.repoProd)
  include(modules.configs)
  include(modules.prodConfigs)

  object modules {
    def roles: RoleModuleDef = new RoleModuleDef {
      // The `ladder` role
      makeRole[CampaignRole]
      // Add bundled roles: `help` & `configwriter`
      include(BundledRolesModule[Task](version = "1.0.0"))
    }

    def api[F[+_, +_]: TagKK]: ModuleDef = new ModuleDef {
      // The `campaign` API
      make[CampaignApi]

      // A set of all APIs
      many[HttpApi]
        .weak[CampaignApi] // add campaign API as a _weak reference_

      make[HttpServer].fromResource[HttpServer.Impl]

      makeTrait[Http4sDsl[Task]]
    }

    def repoDummy: ModuleDef = new ModuleDef {
      tag(Repo.Dummy)

      make[CampaignRepo].from[CampaignRepo.Dummy]
    }

    def repoProd: ModuleDef = new ModuleDef {
      tag(Repo.Prod)

      make[CampaignRepo].from[CampaignRepo.Postgres]

      make[SQL[IO]].from[SQL.Impl[IO]]

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
