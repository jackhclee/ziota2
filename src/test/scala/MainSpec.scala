import org.wiremock.integrations.testcontainers.WireMockContainer
import zio.test._

object MainSpec extends ZIOSpecDefault {
  val wiremockServer: WireMockContainer = new WireMockContainer("wiremock/wiremock:3.12.1")
  wiremockServer.withMappingFromResource("mappings/helloworld.json")
  wiremockServer.start()

  def spec =
    suite("MainSpec")(
      test("a is a") (
       assertTrue(requests.get(s"http://localhost:${wiremockServer.getPort}/helloworld").text() == "Hello, world!")
      )
    )
}