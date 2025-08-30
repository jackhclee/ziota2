object Calculator {

  def add(a: Int, b: Int) = {
    val result = if (a > 0) a + b else a * b
    result
  }

  def select(a: Int): String = {
    a match {
      case a if a == 1 => "A"
      case a if a == 2 => "B"
    }
  }

  case class Ma(name: String, age: Int)

  def createList() = {
    List(Ma("A", 99), Ma("B", 100)).map((1 ,_)).map(_._2.age)
  }
}
