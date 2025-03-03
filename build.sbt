scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"             % "2.1.16",
  "dev.zio"       %% "zio-json"        % "0.7.36",
  "io.getquill"   %% "quill-jdbc-zio"  % "4.8.5",
  "com.h2database" % "h2"              % "2.3.232",
  "ch.qos.logback" % "logback-classic" % "1.5.17",
 "dev.zio" %% "zio-profiling" % "0.3.2"
)

libraryDependencies += compilerPlugin("dev.zio" %% "zio-profiling-tagging-plugin" % "0.3.2")
