scalaVersion := "2.13.16"


organization := "xyz"
version := "0.2.0"

enablePlugins(JavaAppPackaging)

val mainLib = Seq(
  "ch.qos.logback"         % "logback-classic"    % "1.5.17",
  "com.github.pureconfig" %% "pureconfig"         % "0.17.8",
  "com.h2database"         % "h2"                 % "2.3.232",
  "dev.zio"               %% "zio"                % Version.zio,
  "dev.zio"               %% "zio-cli"            % "0.7.1",
  "dev.zio"               %% "zio-http"           % "3.1.0",
  "dev.zio"               %% "zio-json"           % "0.7.36",
  "dev.zio"               %% "zio-logging-slf4j2" % "2.5.0",
  "dev.zio"               %% "zio-prelude"        % "1.0.0-RC39",
  "dev.zio"               %% "zio-profiling"      % "0.3.2",
  "io.getquill"           %% "quill-jdbc-zio"     % "4.8.5"
)

val testLib = Seq(
  "org.wiremock.integrations.testcontainers"      % "wiremock-testcontainers-module" % "1.0-alpha-14" % Test,
  "dev.zio"               %% "zio-test"           % Version.zio % Test,
  "dev.zio"               %% "zio-test-sbt"       % Version.zio % Test,
  "dev.zio"               %% "zio-test-magnolia"  % Version.zio % Test
)

libraryDependencies ++= mainLib ++ testLib

libraryDependencies += compilerPlugin("dev.zio" %% "zio-profiling-tagging-plugin" % "0.3.2")

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")


