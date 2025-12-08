import io.getquill.sql.Common
import zio.cli._
import zio.cli.HelpDoc.Span.text
import zio.Console.printLine
import zio.Console
import ziota.Common

import java.time.LocalDate
// object of your app must extend ZIOCliDefault
object Cli extends ZIOCliDefault {

  sealed trait SubCmd
  case class Sql(local: Boolean, user: Boolean, mode: String, msg: String, date: LocalDate, lbi: List[BigInt]) extends SubCmd
  case class Check(local: Boolean, user: Boolean, mode: String, msg: String, date: LocalDate, lbi: List[BigInt]) extends SubCmd

  /**
   * First we define the commands of the Cli. To do that we need:
   *    - Create command options
   *    - Create command arguments
   *    - Create help (HelpDoc) 
   */
  val options1: Options[Boolean] = Options.boolean("local").alias("l")
  val options2: Options[Boolean] = Options.boolean("user").alias("u")
  val options3: Options[String] = Options.text("mode").alias("m")
  val arguments1: Args[String] = Args.text("repository")
  val arguments2: Args[LocalDate] = Args.localDate("date")
  val arguments3: Args[List[BigInt]] = Args.integer("int").between(1, 2)

  val help: HelpDoc = HelpDoc.p("Creates a copy of an existing repository")

  val command = Command("git")
    .subcommands(
      Command("sql", options1 ++ options2 ++ options3, arguments1 ++ arguments2 ++ arguments3).withHelp(help).map(optsAndArgs => Sql(optsAndArgs._1._1, optsAndArgs._1._2, optsAndArgs._1._3, optsAndArgs._2._1, optsAndArgs._2._2, optsAndArgs._2._3)),
      Command("check", options1 ++ options2 ++ options3, arguments1 ++ arguments2 ++ arguments3).withHelp(help).map(optsAndArgs => Check(optsAndArgs._1._1, optsAndArgs._1._2, optsAndArgs._1._3, optsAndArgs._2._1, optsAndArgs._2._2, optsAndArgs._2._3))
    )

  // Define val cliApp using CliApp.make
  val cliApp = CliApp.make(
    name = "Sample Git",
    version = "1.1.0",
    summary = text("Sample implementation of git clone"),
    command = command
  ) {
    // Implement logic of CliApp
    case Sql(local, user, mode, args, date, bi) => {
      printLine(s"executing git clone options: $local $user $mode args: $args $date $bi") *>
        Console.printLine(s"*********** ${ziota.Common.hello} *************") *>
        MainProg.run
    }
    case Check(local, user, mode, args, date, bi) =>
      printLine(s"executing git check options: $local $user $mode args: $args $date $bi")
  }
}