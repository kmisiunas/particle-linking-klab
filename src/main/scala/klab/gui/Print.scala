package klab.gui

import klab.gui.repl.Colors
import java.io.PrintStream

/**
 * User: karolis@misiunas.com
 * Date: 12/08/2013
 * Time: 01:38
 */
object Print {

  /** shows debug messages */
  var globalDebug: Boolean = false

  /** suppresses any feedback */
  var globalSilent: Boolean = false


  /** Print steam for all output */
  var printMethod: String => Unit = Console.out.println

  private def print = if (globalSilent) {(_:String) => Unit} else printMethod

  /** consistent with scala notation */
  def println(a: Any) = print(Colors.print + a + Colors.end)

  def apply(a: Any) = print(Colors.print + a + Colors.end)

  def simple(a: Any) = print(a.toString)

  def log(key: String, msg: String) = key match {
    case "info" => println("[" + key + "] " + msg)
    case "error" => error("[" + key + "] " + msg)
    case "warning" => error("[" + key + "] " + msg)
    case "help" => help("[" + key + "] " + msg)
    case _ => println("[" + key + "] " + msg)
  }

  /** debug message */
  def debug[A](a: => A, label:String = ""): A =
    if (Print.globalDebug) {print(Colors.debug+ "[debug] " + label + " " + a + Colors.end); a} else a

  // todo: auto logging?
  /** System warning for error */
  def error(a: Any) = print(Colors.error + "Error: " + a + Colors.end)

  /** help print */
  def help(a: Any) = print(Colors.help + a + Colors.end)

}

