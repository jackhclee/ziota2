import io.cucumber.junit.platform.engine.Constants._
import org.junit.platform.suite.api._

@Suite
@IncludeEngines(Array("cucumber"))
@SelectPackages(Array("cucumber"))
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "classpath:cucumber")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "cucumber")
@ConfigurationParametersResource("junit-platform.properties")
class Cucumber {
}
