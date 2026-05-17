import ReleaseTransformations.*
import sbtrelease.ReleasePlugin.*
import sbt.*
import Keys.*
import sbt.complete.DefaultParsers.*
import sbt.complete.Parser
import sbtrelease.Version.Bump
import sbtrelease.Version
import sbtrelease.expectedSnapshotVersionError

ThisBuild / scalaVersion := "2.13.18"

val scala212 = "2.12.20"
val scala213 = "2.13.18"

val supportedScalaVersions = List(scala212, scala213)

ThisBuild / crossScalaVersions := supportedScalaVersions

ThisBuild / organization := "xyz"
ThisBuild / name := "ziota2"

enablePlugins(JavaAppPackaging)

val mainLib = Seq(
  "ch.qos.logback"         % "logback-classic"    % "1.5.17",
  "com.github.pureconfig" %% "pureconfig"         % "0.17.8",
  "com.h2database"         % "h2"                 % "2.3.232",
  "org.postgresql"         % "postgresql"         % "42.7.7",
  "dev.zio"               %% "zio-aws-rds"        % "7.31.51.1",
  "dev.zio"               %% "zio"                % Version.zio,
  "dev.zio"               %% "zio-cli"            % "0.7.2",
  "dev.zio"               %% "zio-http"           % "3.1.0",
  "dev.zio"               %% "zio-json"           % "0.7.36",
  "dev.zio"               %% "zio-logging-slf4j2" % "2.5.0",
  "dev.zio"               %% "zio-prelude"        % "1.0.0-RC39",
  "dev.zio"               %% "zio-profiling"      % "0.3.2",
  "io.getquill"           %% "quill-jdbc-zio"     % "4.8.5",
  "com.zaxxer"             % "HikariCP"           % "6.3.0",
  "com.github.pemistahl"   % "lingua"             % "1.2.2",
  "org.typelevel"         %% "cats-core"          % "2.13.0",
  "org.json4s"            %% "json4s-native"      % "4.1.0-M8",
  "org.json4s"            %% "json4s-jackson"     % "4.1.0-M8",
  "org.apache.jena"        % "jena-arq"           % "5.4.0",
  "org.flywaydb"           % "flyway-core"        % "11.19.0",
  "org.flywaydb"           % "flyway-database-postgresql" % "11.19.0" % "runtime",
  "commons-cli"            % "commons-cli"        % "1.11.0"
)

val testLib = Seq(
  "org.wiremock.integrations.testcontainers"        % "wiremock-testcontainers-module" % "1.0-alpha-15",
  "org.testcontainers"     % "testcontainers"       % "2.0.4",
  "org.testcontainers"     % "testcontainers-postgresql"           % "2.0.4",
  "dev.zio"               %% "zio-test"             % Version.zio,
  "dev.zio"               %% "zio-test-sbt"         % Version.zio,
  "dev.zio"               %% "zio-test-magnolia"    % Version.zio,
  "com.github.sbt.junit"   % "jupiter-interface"    % "0.18.0"   ,
  "io.cucumber"           %% "cucumber-scala"       % "8.39.1"   ,
  "io.cucumber"            % "cucumber-junit-platform-engine"     % "7.34.3",
  "org.junit.jupiter"      % "junit-jupiter-engine" % "6.0.3",
  "org.junit.jupiter"      % "junit-jupiter-api"    % "6.0.3",
  "org.junit.platform"     % "junit-platform-suite" % "6.0.3"
).map(_ % Test)

lazy val common = (project in file("module/common"))
  .settings(
    name := "common",
  )

lazy val root = (project in file("."))
  .dependsOn(common % "compile->compile;test->test")
  .aggregate(common)

libraryDependencies ++= mainLib ++ testLib

libraryDependencies += compilerPlugin("dev.zio" %% "zio-profiling-tagging-plugin" % "0.3.3")

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

releaseVersion := { rawVersion =>
//  Version(rawVersion).map(v =>
//     v.string).getOrElse("XXX")
  rawVersion
}

releaseProcess := Seq[ReleaseStep](
  inquireVersions,
  setReleaseVersion,
  setNextVersion
)

resolvers += Resolver.sonatypeCentralRepo("releases")


Test / parallelExecution := false
Test / fork := true
Test / javaOptions ++= Seq("-Dquill.binds.log=true")
Test / testOptions += Tests.Argument(jupiterTestFramework, "-q", "-v")
