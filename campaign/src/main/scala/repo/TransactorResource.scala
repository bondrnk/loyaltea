package loyaltea
package repo

import config.*
import scala.concurrent.ExecutionContext

import cats.effect.{Async, Resource, Sync}
import distage.{Id, Lifecycle}
import doobie.Transactor
import doobie.hikari.HikariTransactor
import izumi.distage.model.provisioning.IntegrationCheck
import izumi.fundamentals.platform.integration.{PortCheck, ResourceCheck}
import logstage.LogIO
import org.flywaydb.core.Flyway
import zio.*
import zio.interop.catz.*

final class TransactorResource(
    cfg: PostgresCfg,
    portCheck: PortCheck,
    logger: LogIO[Task],
    blockingExecutionContext: ExecutionContext @Id("io"),
) extends Lifecycle.OfCats[Task, Transactor[Task]](
      for {
        transactor <- HikariTransactor.newHikariTransactor[Task](
                        driverClassName = "org.postgresql.Driver",
                        url = cfg.jdbcUrlRaw,
                        user = cfg.user,
                        pass = cfg.password,
                        connectEC = blockingExecutionContext,
                      )
        _          <- Resource.eval(logger.info(s"Hikari transactor started"))
        _          <- Resource.eval(
                        ZIO
                          .attempt {
                            Flyway
                              .configure()
                              .dataSource(cfg.jdbcUrlRaw, cfg.user, cfg.password)
                              .baselineOnMigrate(true)
                              .baselineVersion("0")
                              .locations("classpath:migrations")
                              .load()
                              .migrate()
                          }
                          .tapError(error => logger.error(s"Migration failed with $error"))
                      )
        _          <- Resource.eval(logger.info(s"Migration finished"))
      } yield transactor
    )
    with IntegrationCheck[Task] {
  override def resourcesAvailable(): Task[ResourceCheck] = Sync[Task].delay {
    portCheck.checkPort(
      cfg.host,
      cfg.port,
      s"Couldn't connect to postgres at host=${cfg.host} port=${cfg.port}",
    )
  }
}
