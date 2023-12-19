package loyaltea
package plugins

import api.*
import config.*
import consumers.*
import http.HttpServer
import producers.*
import repo.*
import scala.concurrent.duration.*

import distage.StandardAxis.Repo
import distage.config.ConfigModuleDef
import distage.plugins.PluginDef
import distage.{ModuleDef, Scene}
import doobie.util.transactor.Transactor
import izumi.distage.roles.bundled.BundledRolesModule
import izumi.distage.roles.model.definition.RoleModuleDef
import izumi.fundamentals.platform.integration.PortCheck
import smithy4s.campaign.*
import zio.*

object CampaignPlugin extends PluginDef {
  include(modules.roles)
  include(modules.api)
  include(modules.repoDummy)
  include(modules.repoProd)
  include(modules.configs)

  object modules {
    def roles: RoleModuleDef = new RoleModuleDef {
      // The `campaign` role
      makeRole[CampaignRole]
      // Add bundled roles: `help` & `configwriter`
      include(BundledRolesModule[Task](version = "1.0.0"))
    }

    def api: ModuleDef = new ModuleDef {
      // The `campaign` API
      make[Consumers]
      make[Producers]
      make[CampaignHttpApi]
      make[CampaignApi].from[CampaignApi.Live]
      make[SwaggerHttpApi]
      make[CampaignService[Task]].from[CampaignServiceImpl]

      many[HttpApi]
        .add[CampaignHttpApi]
        .add[SwaggerHttpApi]

      make[HttpServer].fromResource[HttpServer.Impl]
    }

    def repoDummy: ModuleDef = new ModuleDef {
      tag(Repo.Dummy)

      make[CampaignRepo].from[CampaignRepo.Dummy]
      make[UserCampaignRepo].from[UserCampaignRepo.Dummy]
    }

    def repoProd: ModuleDef = new ModuleDef {
      tag(Repo.Prod)

      make[DatabaseTemplate]
      make[CampaignRepo].from[CampaignRepo.Postgres]
      make[UserCampaignRepo].from[UserCampaignRepo.Postgres]
      make[Transactor[Task]].fromResource[TransactorResource]
      make[PortCheck].from(new PortCheck(3.seconds))
    }

    val configs: ConfigModuleDef = new ConfigModuleDef {
      makeConfig[PostgresCfg]("postgres")
      makeConfig[KafkaCfg]("kafka")
    }
  }
}
