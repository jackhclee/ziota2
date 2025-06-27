import zio.ZIO
import zio.test.{ZIOSpecDefault, assertTrue}

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
class Ball(name: String)

@SerialVersionUID(100L)
class SBall(val name: String) extends scala.Serializable



object IoStreamSpec extends ZIOSpecDefault {
  def calculate(n: Int) =
    (if (n % 2 == 0) ZIO.succeed(1 / n).tapDefect(t => ZIO.logError(s"$t")) else ZIO.fail("Input could not be odd"))

  def format(n: Int) = if (n % 2 == 0) ZIO.succeed(0) else ZIO.fail(1)

  val file = "SBall.out"
  val os = new ObjectOutputStream(new FileOutputStream(file))
  val is = new ObjectInputStream(new FileInputStream(file))
  def spec = suite("suite")(
    test("serialize") (
      for {
       ball         <- ZIO.attempt(new SBall("A"))
       _            <- ZIO.attempt(os.writeObject(ball))
       ballRestored <- ZIO.attempt(is.readObject().asInstanceOf[SBall])
       _            <- ZIO.logInfo(s"$ballRestored")
       a            <- (calculate(2) *> format(1).mapError(i => new Exception(s"format returns: $i")).catchNonFatalOrDie(e => ZIO.logError(s"$e"))).catchAllDefect(e => ZIO.logError(s"$e"))
      } yield (assertTrue(true))
    )
  )
}
