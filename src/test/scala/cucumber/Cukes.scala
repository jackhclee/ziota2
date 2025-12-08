package cucumber

import io.cucumber.scala.{ScalaDsl, EN}
import org.junit.jupiter.api.Assertions._

class Cukes extends ScalaDsl with EN {

  var cukeCnt = 0

  Given("I have {int} cukes in my belly") { cukes: Int =>
    println(s"Cukes: $cukes")
    cukeCnt = cukes
  }

  When("I take {int} cukes from my belly") { cukes: Int =>
    cukeCnt = cukeCnt - cukes
  }

  And("I take {int} cukes and then again {int} from my belly") { (cukes: Int, cukes2: Int) =>
    println(s"Cukes: $cukes $cukes2")
    println(s"cukeCnt: $cukeCnt")
    cukeCnt = cukeCnt - cukes - cukes2
  }

  Then("I should have {int} cukes in my belly") { cukes: Int =>
    assertTrue(cukeCnt == cukes)
  }

}
