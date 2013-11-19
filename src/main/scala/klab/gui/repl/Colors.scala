package klab.gui.repl

/**
 * == command line color scheme for terminal ==
 *
 * todo: change entire model for coloring
 *
 * User: karolis@misiunas.com
 * Date: 30/07/2013
 * Time: 14:54
 */
object Colors {

  /** terminator for selected colors */
  def end = colors("end")

  /** error message */
  def error = colors("end")

  def prompt = colors("prompt")

  /** color when messages are printed automatically */
  def autoPrompt = "\033[36m"

  /** color when messages are printed automatically */
  def print = colors("print")

  /** color when messages are printed automatically */
  def help = colors("help")

  /** Color map for the system */
  lazy val colors:  Map[String,String] = unix
    //if(System.getProperty("os.name").toLowerCase.contains("mac")) new Colors() else new Colors()

  /** Unix compatible color codes (works with PowerShell) */
  val unix: Map[String,String] = Map(
    "end" -> "\033[0m",
    "error" -> "\033[91m",
    "prompt" -> "\033[36m",
    "print" -> "\033[33m",
    "help" -> "\033[33m"
  )

  val neutral: Map[String,String] = Map(
    "end" -> "",
    "error" -> "",
    "prompt" -> "",
    "print" -> "",
    "help" -> ""
  )

}
