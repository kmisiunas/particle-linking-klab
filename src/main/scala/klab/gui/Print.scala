package klab.gui

import klab.gui.repl.Colors
import java.io.PrintStream

/**
 * User: karolis@misiunas.com
 * Date: 12/08/2013
 * Time: 01:38
 */
object Print {

  /** Print steam for all output */
  var printMethod: String => Unit = Console.out.println

  private def print = printMethod

  /** consistent with scala notation */
  def println(a: Any) = print(Colors.print + a + Colors.end)

  def apply(a: Any) = print(Colors.print + a + Colors.end)

  def simple(a: Any) = print(a.toString)

  def log(key: String, msg: String) = println("[" + key + "] " + msg)

  // todo: auto logging?
  /** System warning for error */
  def error(a: Any) = print(Colors.error + "Error: " + a + Colors.end)

  /** help print */
  def help(a: Any) = print(Colors.help + a + Colors.end)

}
