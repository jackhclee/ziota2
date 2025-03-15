scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"             % "2.1.16",
  "dev.zio"       %% "zio-json"        % "0.7.36",
  "io.getquill"   %% "quill-jdbc-zio"  % "4.8.5",
  "com.h2database" % "h2"              % "2.3.232",
  "ch.qos.logback" % "logback-classic" % "1.5.17",
  "dev.zio"       %% "zio-profiling"   % "0.3.2",
  "org.wiremock.integrations.testcontainers" % "wiremock-testcontainers-module" % "1.0-alpha-14" % Test,
  "dev.zio"       %% "zio-test"        % "2.1.16" % Test,
  "dev.zio"       %% "zio-test-sbt"          % "2.1.16" % Test,
  "dev.zio"       %% "zio-test-magnolia"     % "2.1.16" % Test,
  "com.lihaoyi"   %% "requests"        % "0.8.0" % Test
)

libraryDependencies += compilerPlugin("dev.zio" %% "zio-profiling-tagging-plugin" % "0.3.2")

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")


