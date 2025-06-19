import org.testcontainers.containers.GenericContainer
import org.wiremock.integrations.testcontainers.WireMockContainer
import zio.{Scope, ZIO, ZInputStream, ZLayer}
import zio.test._
import zio.http._
import zio.prelude.ZValidation
import zio.test.TestAspect.sequential
import service.{ImageService, LiveImageService}

object MainSpec extends ZIOSpecDefault {
  val wiremockServer: WireMockContainer = new WireMockContainer("wiremock/wiremock:3.12.1")
  wiremockServer.withMappingFromResource("mappings/helloworld.json")
  wiremockServer.start()
  val confluentKafka = new GenericContainer("confluentinc/cp-kafka:7.9.0")
  confluentKafka.start()

  def spec = {
    suite("MainSpec")(
      suite("Group1")(
        test("should get HTTP response from WireMock")(
          for {
            client <- ZIO.serviceWith[Client](_.host("localhost").port(wiremockServer.getPort))
            response <- client.batched(Request.get("/helloworld"))
            body <- response.body.asString
            _ <- ZIO.logInfo(s"$body")
          } yield assertTrue(body == "Hello, world!")
        ).provide(Client.default),
        test("should acquire and release resources successfully")(
          for {
            _ <- ZIO.log(s"Hello")
            _ <- ZIO.scoped(ZIO.acquireRelease(ZIO.logInfo("Get A"))(r => ZIO.logInfo("Release A")))
            _ <- ZIO.log(s"End")
          } yield assertTrue(true)
        ),
        test("should acquire and release resources successfully at end of scope")(
          for {
            _ <- ZIO.log(s"Hello")
            _ <- ZIO.acquireRelease(ZIO.logInfo("Get B"))(r => ZIO.logInfo("Release B"))
            _ <- ZIO.log(s"End")
          } yield assertTrue(true)
        ),
        test("read")(
          for {
            zis   <- ZIO.readFileInputStream("src/test/resources/mappings/helloworld.json")
            bytes <- zis.readAll(8000)
            _     <- ZIO.logInfo(s"${bytes.length}")
            _     <- ZIO.logInfo(bytes.asString)
            zos   <- ZIO.writeFileOutputStream("aa.json")
            _     <- zos.write(bytes)
          } yield assertTrue(true)
        ),
        test("pre-lude")({
          val counter: Int = -1
          val check1 = ZValidation.fromPredicateWith("should be larger than 0")(counter)(_ > 0).log("LOG1")
          val check2 = ZValidation.fromPredicateWith("should be even")(counter)(_ % 2 == 0)

          val result = check1 zipPar check2

          result.mapError( e => {
            println(s"$e")
            e
          } )

          result.getLog.foreach(w => println(s"############ $w"))

          assertTrue(result.isFailure)
        }
        ),
        test("Calculator add") ({
          assertTrue(Calculator.add(1, 2) == 3 && Calculator.add(-1, 2) == -2)
        }
        ),
        test("Calculator select") ({
          assertTrue(Calculator.select(1) == "A" && Calculator.select(2) == "B")
        }
        )
      ),
      suite("Group2")(
        test("g2-t1")(
          for {
           r <- ImageService.version
          } yield assertTrue(r == 1)
        ),
        test("g2-t2")(
          for {
            r <- ImageService.patch
          } yield assertTrue(r == 2)
        )
      ).provide(ZLayer.succeed(new LiveImageService))

    ) @@ TestAspect.sequential @@ TestAspect.timed
  }
}
