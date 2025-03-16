import org.wiremock.integrations.testcontainers.WireMockContainer
import zio.ZIO
import zio.test._
import zio.http._

object MainSpec extends ZIOSpecDefault {
  val wiremockServer: WireMockContainer = new WireMockContainer("wiremock/wiremock:3.12.1")
  wiremockServer.withMappingFromResource("mappings/helloworld.json")
  wiremockServer.start()

  def spec =
    suite("MainSpec")(
      test("should get HTTP response from WireMock") (
        for {
          client   <- ZIO.serviceWith[Client](_.host("localhost").port(wiremockServer.getPort))
          response <- client.batched(Request.get("/helloworld"))
          body     <- response.body.asString
        } yield assertTrue(body == "Hello, world!")
      )
    ).provide(Client.default)
}