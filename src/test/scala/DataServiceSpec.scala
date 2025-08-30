import MainProg.appConfig
import io.getquill.jdbczio.Quill
import zio.{ZIO, ZLayer}
import zio.test.{TestAspect, ZIOSpecDefault, assertTrue}

object DataServiceSpec extends ZIOSpecDefault {

  def spec = suite("DataServiceSpec")(
    test("db")(
      for {
        _        <- DataService.insertPerson(Person("Jack", 1999))
        _        <- DataService.insertPerson(Person("Jack", 2000))
        persons  <- DataService.findPerson("Jack")
      } yield (assertTrue(persons.contains(Person("Jack", 4000))))
    ).provide(
      DataService.live,
      Quill.Postgres.fromNamingStrategy(io.getquill.SnakeCase),
      ZLayer.succeed(DBConfig.getDataSource(appConfig))
    )
  ) @@ TestAspect.timed
}
