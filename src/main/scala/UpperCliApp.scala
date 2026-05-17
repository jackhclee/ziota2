import org.apache.commons.cli._

object UpperCliApp extends App {
  val options = new Options()
  options.addOption(Option.builder("i").longOpt("input").argName("text").hasArg().desc("Optional input text").build())

  val parser = new DefaultParser()
  val formatter = new HelpFormatter()

  try {
    val cmd = parser.parse(options, args)
    var out = "EMPTY"
    if (cmd.hasOption("i")) {
      val v = cmd.getOptionValue("i")
      if (v != null && v.nonEmpty) out = v.toUpperCase
    }
    println(out)
  } catch {
    case e: ParseException =>
      System.err.println(s"Error parsing arguments: ${e.getMessage}")
      formatter.printHelp("UpperCliApp", options)
      sys.exit(1)
  }
}
