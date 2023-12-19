package loyaltea
package config

import com.typesafe.config.*

import java.net.URI

case class PostgresCfg(
    host: String,
    port: Int,
    user: String,
    password: String,
    database: String,
    url: Option[String] = None,
) {
  def jdbcUrlRaw = s"jdbc:postgresql://${host}:${port}/${database}"
  
  def jdbcUrl(host: String, port: Int, path: String, user: String, password: String) =
    s"jdbc:postgresql://$host:$port$path?user=$user&password=$password"

  def jdbcUrl: String =
    url
      .map { url =>
        if (url.startsWith("postgres://")) {
          val dbUri                = new URI(System.getenv("DATABASE_URL"));
          val List(user, password) = dbUri.getUserInfo.split(":").toList
          jdbcUrl(dbUri.getHost, dbUri.getPort, dbUri.getPath, user, password)
        } else url
      }
      .getOrElse(jdbcUrl(host, port, "/" + database, user, password))

  def toConfig: Config = ConfigFactory.parseString(
    s"""
       |dataSourceClassName=org.postgresql.ds.PGSimpleDataSource
       |dataSource.url = "$jdbcUrl"
       |""".stripMargin
  )
}
