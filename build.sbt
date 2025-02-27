scalaVersion := "2.12.20"


libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"             % "2.1.16",
  "io.getquill"   %% "quill-jdbc-zio"  % "4.8.5",
  "com.h2database" % "h2"              % "2.3.232",
  "ch.qos.logback" % "logback-classic" % "1.5.17"
)