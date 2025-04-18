import zio.cli._
import zio.cli.HelpDoc.Span.text
import zio.Console.printLine

// object of your app must extend ZIOCliDefault
object MyGit extends ZIOCliDefault {

  /**
   * First we define the commands of the Cli. To do that we need:
   *    - Create command options
   *    - Create command arguments
   *    - Create help (HelpDoc) 
   */
  val optionsForce: Options[Boolean] = Options.boolean("force").alias("f")
  val argumentsUser: Args[String] = Args.text("user")
  val help: HelpDoc = HelpDoc.p("Creates a copy of an existing repository")

  sealed trait Cmd
  case class Login(user: String, force: Boolean) extends Cmd
  case class Logout(user: String) extends Cmd
  case class Special(user: String) extends Cmd

  val command = //: Command[(Boolean, String)] =
    Command("mygit")
      .subcommands(
        Command("login", optionsForce, argumentsUser).withHelp("login to repository").map(e => Login(e._2, e._1)),
        Command("logout", argumentsUser).withHelp("logout to repository").map(e => Logout(e)))

  // Define val cliApp using CliApp.make
  val cliApp = CliApp.make(
    name = "My Git",
    version = "1.1.0",
    summary = text("Sample implementation of git clone"),
    command = command
  ) {
      // Implement logic of CliApp
      case Login(force, user) => printLine(s"executing Login $user $force")
      case Logout(user) => printLine(s"executing Logout $user")
  }

}