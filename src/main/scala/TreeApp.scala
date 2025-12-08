import zio.{ZIO, ZIOAppDefault, ZLayer}
import cats.Show

case class Engine(power: Int)
case class Car(engine: Engine)

object TreeApp extends ZIOAppDefault {
  def run = (for {
    car <- ZIO.service[Car]
    _   <- ZIO.logInfo(s"$car")
    _   <- ZIO.logInfo(s"${print(car)}")
  } yield ()).provide(ZLayer.make[Car](
   // ZLayer.succeed(Engine("ha")),
    ZLayer.fromZIO(
      for {
        ne <- ZIO.attempt(Engine(power = 5000))
//        e <- ZIO.service[Engine]
      } yield Car(ne)
    )
  ))

  def print(car: Car): String = {
    implicit val carShow: Show[Car] = Show.show(car => s"*** Car (Engine: power => ${car.engine.power}) ***")
    import cats.syntax.all._
    car.show
  }
}
