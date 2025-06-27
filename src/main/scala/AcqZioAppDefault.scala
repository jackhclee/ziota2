import zio.ZIOAppDefault
import zio.{ZIO, Console}

object AcqZioAppDefault extends ZIOAppDefault {
  override def run = {
    for {
      _ <- ZIO.logInfo("Start")
      _ <- ZIO.acquireRelease(ZIO.logInfo(s"acquire HAPPY") *> ZIO.succeed("HAPPY"))(str => ZIO.log(s"$str"))
      _ <- ZIO.logInfo("End")
      _ <- ZIO.addFinalizer(ZIO.logInfo("additional finalizer"))
      _ <- ZIO.scopedWith(s => ZIO.logInfo("scopedWith"))
    } yield ()
  }
}
