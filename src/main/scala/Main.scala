import io.getquill.{H2ZioJdbcContext, Query, SnakeCase}
import io.getquill.jdbczio.Quill
import zio.Exit.Success
import zio.json.{DecoderOps, DeriveJsonDecoder, DeriveJsonEncoder, EncoderOps, JsonDecoder, JsonEncoder}
import zio.profiling.sampling.SamplingProfiler
import zio.profiling.sampling.SamplingProfiler._
import zio.{Duration, ExitCode, Scope, ZIO, ZIOApp, ZIOAppArgs, ZIOAppDefault, ZLayer}

import java.sql.SQLException

case class Person(name: String, age: Int)
case class Squad(members: List[Person])

object Person {
  implicit val decoder: JsonDecoder[Person] = DeriveJsonDecoder.gen[Person]
  implicit val encoder: JsonEncoder[Person] = DeriveJsonEncoder.gen[Person]
}

object Squad {
  implicit val decoder: JsonDecoder[Squad] = DeriveJsonDecoder.gen[Squad]
  implicit val encoder: JsonEncoder[Squad] = DeriveJsonEncoder.gen[Squad]
}

class DataService(quill: Quill.H2[SnakeCase]) {
  import quill._

  def report() = run (
     sql"""SELECT max(age) FROM person""".as[Query[Int]]
  )
  def insertTwoPersons(person1: Person, person2: Person): ZIO[Any, Throwable, List[Person]] = {
      val action1 = run(query[Person].insertValue(lift(person1)))
      val action2 = run(query[Person].insertValue(lift(person2)))
      quill.transaction {
        for {
          _ <- action1
          _ = if (person2.name.equals("John")) { throw new RuntimeException("Never store John")}
          _ <- action2
          p <- run(query[Person])
        } yield p
      }
    }
  def updatePerson(name: String, age: Int): ZIO[Any, SQLException, Long] = run { query[Person].filter(_.name.equals(lift(name))).updateValue(lift(Person(name, age))) }
  def findPerson(name: String): ZIO[Any, SQLException, List[Person]] = run { query[Person].filter(_.name.equals(lift(name)))}
  def getPeople: ZIO[Any, SQLException, List[Person]] = run(query[Person])
  def insertPerson(person: Person): ZIO[Any, SQLException, Long] = run(query[Person].insertValue(lift(person)))
}


object DataService {
  def report() = {
//    ZIO.sleep(Duration.fromMillis(2 * 1000)) *>
      ZIO.serviceWithZIO[DataService](_.report())
  }
  def insertTwoPersons(person1: Person, person2: Person) = ZIO.serviceWithZIO[DataService](_.insertTwoPersons(person1, person2))
  def updatePerson(name: String, age: Int) = ZIO.serviceWithZIO[DataService](_.updatePerson(name, age))
  def findPerson(name: String) = ZIO.serviceWithZIO[DataService](_.findPerson(name))
  def getPeople= {
//      ZIO.sleep(Duration.fromMillis(2 * 1000)) *>
      ZIO.serviceWithZIO[DataService](_.getPeople)
  }

  def insertPerson(person: Person) = {
    ZIO.logInfo(s"Inserting Person: $person") *>
    ZIO.serviceWithZIO[DataService](_.insertPerson(person))
  }

  //def definteTable = ZIO.serviceWithZIO[DataService](_.defineTable)
  val live = ZLayer.fromFunction(new DataService(_))
}

object Main extends ZIOAppDefault {

  import Squad._

  def makeJson(squad: Squad) = {
    val jsonStr = squad.toJson
    jsonStr.fromJson[Squad]
    jsonStr
  }

  val samplingPeriodMs = 1
  override def run = SamplingProfiler(Duration.fromMillis(samplingPeriodMs)).profile {
    (for {
      before <- DataService.findPerson("Jack")
      _      <- DataService.insertPerson(Person("Long", 1999))
      _      <- DataService.insertTwoPersons(Person("Jack", 1999), Person("John", 1999)).catchAllDefect(e => ZIO.logError(e.getMessage))
      after  <- DataService.findPerson("Jack")
      _      <- DataService.updatePerson("Jack", 2999)
      count  <- DataService.report()
      end    <- DataService.getPeople
      _      <- ZIO.logInfo(s"***************")
      _      <- ZIO.logInfo(s"before: ${before.size} $before")
      _      <- ZIO.logInfo(s"after:  ${after.size} $after")
      _      <- ZIO.logInfo(s"count   $count")
      _      <- ZIO.logInfo(s"end:    $end")
      _      <- ZIO.logInfo(s"***************")
      _      <- ZIO.logInfo(makeJson(Squad(List(Person("J", 2000)))))
    } yield ())
      .provide(
        DataService.live,
        Quill.H2.fromNamingStrategy(SnakeCase),
        Quill.DataSource.fromPrefix("h2")
    )
      .debug("Results")
      .exitCode

  }.flatMap(h => h.stackCollapseToFile(s"profile.$samplingPeriodMs.folded"))
}