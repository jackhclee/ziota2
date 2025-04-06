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
}
