import OnceProg.x
import org.testcontainers.shaded.org.hamcrest.Matchers
import zio.test.{TestClock, TestRandom, ZIOSpecDefault, assertTrue}
import zio.{Duration, IO, Random, Task, UIO, URIO, ZIO}

object ZErrorSpec extends ZIOSpecDefault {

  val fail: IO[Exception, Nothing] = ZIO.fail(new Exception("Failed"))

  val die: UIO[Nothing] = ZIO.die(new Exception("Die"))

  def failOrDie(i: Int): IO[Exception, Nothing] = if (i % 2 == 1) ZIO.fail(new Exception("fail becoz odd input")) else ZIO.die(new Exception("die becoz even"))

  def longFiber(x: Int) =
    for {
      _ <- ZIO.logInfo(s"Starting a fiber and sleep $x seconds")
      _ <- ZIO.sleep(Duration.fromSeconds(2))
      _ <- ZIO.logInfo(s"Ending fiber after sleep $x seconds")
    } yield ()

      //  val request: Task[Response] = ZIO.attempt({
      //    println(s"DDDD ${Thread.currentThread()}")
      //    throw new Exception(s"${Thread.currentThread()}")
      //    requests.get("https://localhost/test")
      //  })

  val log: UIO[Unit] = ZIO.log("Test").catchAllDefect(e => ZIO.log(s"$e"))

  val k: Task[Unit] = ZIO.attempt(())

  override def spec =
    suite("A Suite") (
    test("catchNonFatalOrDie") (
      for {
        z <- fail.catchNonFatalOrDie(_ => ZIO.logError("Rescued"))
      } yield assertTrue(z == ())
    ),
    test("die") (
      for {
        z <- die.catchAllDefect(_ => ZIO.logError("Rescued"))
      } yield assertTrue(z == ())
    ),
    test("illegalargumenterror") (
      for {
        z <- ZIO.fail(new IllegalArgumentException("IAE"))
          .orDie
          //.catchAll(_ => ZIO.logError("catchAll IAE"))
          .catchAllDefect(e => ZIO.logError(s"catchAllDefect IAE $e"))
      } yield assertTrue(z == ())
    ),
    test("failOrdie") (
      for {
        z <- failOrDie(1).catchNonFatalOrDie(_ => ZIO.logError("Rescued"))
      } yield assertTrue(z == ())
    ),
    test("wait") (
      for {
        z <- longFiber(2).onInterrupt(ZIO.logInfo(s"Fiber interrupted")).fork
//        exit <- z.interrupt
        _ <- TestClock.adjust(Duration.fromSeconds(2))
        exit <- z.join
      } yield assertTrue(exit == ())
    ),
    test("a") (
      for {
        e <- ZIO.attempt("a").exit
        c <- if (e.isSuccess) ZIO.attempt("A") else ZIO.attempt("B")
      } yield assertTrue(c == "A")
    ),
    test("k")(
      {
        val pf: PartialFunction[Int, String] = {
          case x if x > 0 => "+"
          case x if x < 0 => "-"
        }

        for {
          c <- ZIO.using(ZIO.succeed("AAAAAA"))(a => ZIO.logInfo(s"$a from resources"))
          r <- ZIO.succeed(pf(1))
        } yield assertTrue(c == () && r == "+")
      }
    ),
    test("aa")(
      {
        for {
          _ <- TestRandom.feedInts(2)
          i <- Random.nextInt
          _ <- ZIO.logInfo(s"$i")
          v <- (if (i % 2 == 1) ZIO.succeed("A") else ZIO.fail("fail")).catchAll(t => ZIO.logError(s"Error"))
          str <- ZIO.acquireRelease(ZIO.succeed("A"))(a => ZIO.logInfo(s"$a is released"))
        } yield assertTrue(1 == 1)
      }
    ),

//    test("request") (
//      for {
//        z <- request.catchNonFatalOrDie(e => ZIO.log(s"$e"))//catchAllCause(e => ZIO.logError(s"e.isFailure: ${e.isFailure} ${e.failures}")).catch
//      } yield assertTrue(z == ())
//    )
  )
}
