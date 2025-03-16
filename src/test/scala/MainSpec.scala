import org.wiremock.integrations.testcontainers.WireMockContainer
import zio.{Scope, ZIO}
import zio.test._
import zio.http._

object MainSpec extends ZIOSpecDefault {
  val wiremockServer: WireMockContainer = new WireMockContainer("wiremock/wiremock:3.12.1")
  wiremockServer.withMappingFromResource("mappings/helloworld.json")
  wiremockServer.start()

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
        )
      )
    ) @@ TestAspect.sequential
  }
}