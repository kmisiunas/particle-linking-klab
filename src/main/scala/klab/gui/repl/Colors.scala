package klab.gui.repl

/**
 * == command line color scheme for terminal ==
 *
 * todo: change entire model for coloring
 *
 *  CLEAR   = "\e[0m"
    BOLD    = "\e[1m"

    # Colors
    BLACK   = "\e[30m"
    RED     = "\e[31m"
    GREEN   = "\e[32m"
    YELLOW  = "\e[33m"
    BLUE    = "\e[34m"
    MAGENTA = "\e[35m"
    CYAN    = "\e[36m"
    WHITE   = "\e[37m"
 *
 * User: karolis@misiunas.com
 * Date: 30/07/2013
 * Time: 14:54
 */
object Colors {

  /** terminator for selected colors */
  def end = colors("end")

  /** error message */
  def error = colors("error")

  def prompt = colors("prompt")

  /** color when messages are printed automatically */
  def autoPrompt = colors("prompt")

  /** color when messages are printed automatically */
  def print = colors("print")

  /** color when messages are printed automatically */
  def help = colors("help")

  def res = colors("res")

  /** Color map for the system */
  lazy val colors:  Map[String,String] =
    if(System.getProperty("os.name").toLowerCase.contains("windows")) neutral else unix

  /** Unix compatible color codes (works with PowerShell) */
  val unix: Map[String,String] = Map(
    "end" -> "\033[0m",
    "error" -> "\033[31m",
    "prompt" -> "\033[36m",
    "print" -> "\033[33m",
    "help" -> "\033[33m",
    "warning" -> "\033[33m",
    "res" -> "\033[33m"
  )

  val neutral: Map[String,String] = Map(
    "end" -> "",
    "error" -> "",
    "prompt" -> "",
    "print" -> "",
    "help" -> "",
    "warning" -> "",
    "res" -> ""
  )

}
