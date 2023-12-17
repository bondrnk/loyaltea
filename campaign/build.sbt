val V = new {
  val Scala     = "3.3.1"
  val Izumi     = "1.2.3"
  val Quill     = "4.8.0"
  val Doobie    = "1.0.0-RC2"
  val Smithy    = "0.18.3"
  val Circe     = "0.14.6"
  val Mockito   = "5.8.0"
  val Http4s    = "0.23.24"
  val Zio       = "2.0.20"
  val ZioKafka  = "2.7.1"
  val ZioCats   = "23.1.0.0"
  val Chimney   = "0.8.3"
  val Jwt       = "9.4.5"
  val Bcrypt    = "4.3.0"
  val OsLib     = "0.9.1"
  val JavaxMail = "1.6.2"
}

inThisBuild(
  List(
    scalaVersion     := V.Scala,
    organization     := "loyaltea",
    organizationName := "loyaltea",
    idePackagePrefix := Some("loyaltea"),
  )
)

addCommandAlias("fmt", "; compile:scalafmt; test:scalafmt; scalafmtSbt")
addCommandAlias("fmtCheck", "; compile:scalafmtCheck; test:scalafmtCheck; scalafmtSbtCheck")

lazy val coreDeps = Seq(
  "io.circe"             %% "circe-core"    % V.Circe,
  "io.circe"             %% "circe-generic" % V.Circe,
  "io.circe"             %% "circe-parser"  % V.Circe,
  "io.scalaland"         %% "chimney"       % V.Chimney,
  "com.github.jwt-scala" %% "jwt-core"      % V.Jwt,
  "com.github.jwt-scala" %% "jwt-circe"     % V.Jwt,
  ("com.github.t3hnar"   %% "scala-bcrypt"  % V.Bcrypt).cross(CrossVersion.for3Use2_13),
  "com.lihaoyi"          %% "os-lib"        % V.OsLib,
  "com.lihaoyi"          %% "os-lib-watch"  % V.OsLib,
  "com.sun.mail"          % "javax.mail"    % V.JavaxMail,
  "org.mockito"           % "mockito-core"  % "5.8.0" % Test,
)

lazy val zioDeps = Set(
  "dev.zio" %% "zio"               % V.Zio,
  "dev.zio" %% "zio-streams"       % V.Zio,
  "dev.zio" %% "zio-interop-cats"  % V.ZioCats,
  "dev.zio" %% "zio-test"          % V.Zio % "test",
  "dev.zio" %% "zio-test-sbt"      % V.Zio % "test",
  "dev.zio" %% "zio-test-magnolia" % V.Zio % "test",// optional
)

lazy val kafkaDeps = Seq(
  "dev.zio" %% "zio-kafka"         % V.ZioKafka,
  ("dev.zio" %% "zio-kafka-testkit" % V.ZioKafka % Test)
    .exclude("org.scala-lang.modules", "scala-java8-compat_2.13")
    .exclude("com.typesafe.scala-logging", "scala-logging_2.13"),
)

lazy val izumiDeps = Seq(
  "io.7mind.izumi" %% "distage-core"              % V.Izumi,
  "io.7mind.izumi" %% "distage-extension-config"  % V.Izumi,
  "io.7mind.izumi" %% "distage-framework"         % V.Izumi,
  "io.7mind.izumi" %% "distage-framework-docker"  % V.Izumi,
  "io.7mind.izumi" %% "distage-testkit-scalatest" % V.Izumi,
  "io.7mind.izumi" %% "logstage-adapter-slf4j"    % V.Izumi,
  "io.7mind.izumi" %% "logstage-core"             % V.Izumi,
  "io.7mind.izumi" %% "logstage-rendering-circe"  % V.Izumi,
)

lazy val smithyDeps = Seq(
  "com.disneystreaming.smithy4s" %% "smithy4s-http4s"         % V.Smithy,
  "com.disneystreaming.smithy4s" %% "smithy4s-http4s-swagger" % V.Smithy,
)

lazy val postgresDeps = Seq(
  "org.tpolecat" %% "doobie-core"     % V.Doobie,
  "org.tpolecat" %% "doobie-postgres" % V.Doobie,
  "org.tpolecat" %% "doobie-hikari"   % V.Doobie,
  "io.getquill"  %% "quill-jdbc-zio"  % V.Quill,
  "io.getquill"  %% "quill-doobie"    % V.Quill,
)

lazy val http4sDeps = Seq(
  "org.http4s" %% "http4s-dsl"          % V.Http4s,
  "org.http4s" %% "http4s-ember-server" % V.Http4s,
  "org.http4s" %% "http4s-ember-client" % V.Http4s,
  "org.http4s" %% "http4s-circe"        % V.Http4s,
)

lazy val root = (project in file("."))
  .settings(
    name             := "campaign",
    idePackagePrefix := Some("loyaltea"),
    scalacOptions ++= Seq("-Yretain-trees", "-Ykind-projector:underscores"),
    Universal / javaOptions ++= Seq("-u repo:dummy"),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .enablePlugins(smithy4s.codegen.Smithy4sCodegenPlugin)
  .settings(
    libraryDependencies ++= coreDeps ++ zioDeps ++ smithyDeps ++ http4sDeps ++ izumiDeps ++ postgresDeps ++ kafkaDeps
  )
