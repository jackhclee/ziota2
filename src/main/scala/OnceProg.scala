import zio.{Clock, Duration, IO, URIO, ZIO, ZIOAppDefault}

object OnceProg extends ZIOAppDefault {
  val cachedEffect: ZIO[Any, Nothing, IO[Nothing, Long]] = (Clock.instant.map(_.getEpochSecond)).cached(Duration.fromMillis(2 * 1000))

  val x: URIO[Any, String] = ZIO.attempt(1/0).fold(_ => "F", _ => "S")

  val run =
    for {
      _ <- ZIO.logInfo(s"Start")
      x <- cachedEffect
      v <- x
      _ <- ZIO.logInfo(s"$v")
      _ <- ZIO.logInfo("odd time").when(v % 2 == 1)
      _ <- ZIO.logInfo("double time").unless(v % 2 == 1)
      _ <- ZIO.sleep(Duration.fromMillis(3 * 1000))
      y <- x
      _ <- ZIO.logInfo(s"$y")
      _ <- ZIO.sleep(Duration.fromMillis(1 * 1000))
      z <- x
      _ <- ZIO.logInfo(s"$z")
      _ <- ZIO.logInfo(s"End")
    } yield ()
}
