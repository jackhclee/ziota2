import org.wiremock.integrations.testcontainers.WireMockContainer
import zio.test._

object MainSpec extends ZIOSpecDefault {
  val wiremockServer: WireMockContainer = new WireMockContainer("wiremock/wiremock:3.12.1")
//  wiremockServer.withMappingFromResource("mappings/hellowrold.json")
  wiremockServer.withMappingFromJSON("helloworld",
    """
      |{
      |  "request": {
      |    "method": "GET",
      |    "url": "/helloworld"
      |  },
      |
      |  "response": {
      |    "status": 200,
      |    "body": "Hello, world!",
      |    "headers": {
      |      "Content-Type": "text/plain"
      |    }
      |  }
      |}
      |""".stripMargin)
  wiremockServer.start()

  def spec =
    suite("MainSpec")(
      test("a is a") (
       assertTrue(requests.get(s"http://localhost:${wiremockServer.getPort}/helloworld").text() == "1")
      )
    )
}