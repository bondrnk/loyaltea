package loyaltea

import api.CampaignApi
import http.HttpServer
import plugins.*
import scala.annotation.unused

import distage.*
import distage.StandardAxis.Repo
import distage.plugins.PluginConfig
import izumi.distage.model.definition.StandardAxis.Scene
import izumi.distage.roles.RoleAppMain
import izumi.distage.roles.bundled.ConfigWriter
import izumi.distage.roles.model.{RoleDescriptor, RoleService}
import izumi.fundamentals.platform.IzPlatform
import izumi.fundamentals.platform.cli.model.raw.*
import logstage.*
import zio.*

final class CampaignRole(
    @unused runningServer: HttpServer,
    log: LogIO[Task],
) extends RoleService[Task] {
  override def start(roleParameters: RawEntrypointParams, freeArgs: Vector[String]): Lifecycle[Task, Unit] = {
    Lifecycle.liftF(log.info("Campaign API started!"))
  }
}
object CampaignRole extends RoleDescriptor {
  final val id = "campaign"
}

// ./launcher -u repo:dummy :campaign
object MainDummy extends MainBase(Activation(Repo -> Repo.Dummy), Vector(RawRoleParams(CampaignRole.id)))

// ./launcher -u scene:managed :campaign
object MainProdDocker
    extends MainBase(Activation(Repo -> Repo.Prod, Scene -> Scene.Managed), Vector(RawRoleParams(CampaignRole.id)))

// ./launcher :campaign
object MainProd
    extends MainBase(Activation(Repo -> Repo.Prod, Scene -> Scene.Provided), Vector(RawRoleParams(CampaignRole.id)))

// ./launcher :configwriter
object MainWriteReferenceConfigs
    extends MainBase(
      activation = {
        Activation(Repo -> Repo.Prod, Scene -> Scene.Provided)
      },
      requiredRoles = {
        Vector(
          RawRoleParams(
            role = ConfigWriter.id,
            roleParameters = RawEntrypointParams(
              flags = Vector.empty,
              // output configs in "hocon" format, instead of "json"
              values = Vector(RawValue("format", "hocon")),
            ),
            freeArgs = Vector.empty,
          )
        )
      },
    )

object GenericLauncher extends MainBase(Activation(Repo -> Repo.Prod, Scene -> Scene.Provided), Vector.empty)

sealed abstract class MainBase(
    activation: Activation,
    requiredRoles: Vector[RawRoleParams],
) extends RoleAppMain.LauncherBIO[IO] {

  override def requiredRoles(argv: RoleAppMain.ArgV): Vector[RawRoleParams] = {
    requiredRoles
  }

  override def pluginConfig: PluginConfig = {
    if (IzPlatform.isGraalNativeImage) {
      // Only this would work reliably for NativeImage
      PluginConfig.const(List(CampaignPlugin, PostgresDockerPlugin))
    } else {
      // Runtime discovery with PluginConfig.cached might be convenient for pure jvm projects during active development
      // Once the project gets to the maintenance stage it's a good idea to switch to PluginConfig.const
      PluginConfig.cached(pluginsPackage = "loyaltea.plugins")
    }
  }

  override protected def roleAppBootOverrides(argv: RoleAppMain.ArgV): Module =
    super.roleAppBootOverrides(argv) ++ new ModuleDef {
      make[Activation].named("default").fromValue(defaultActivation ++ activation)
    }

  private[this] def defaultActivation = Activation(Scene -> Scene.Provided)

}
