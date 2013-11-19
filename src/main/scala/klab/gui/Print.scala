package klab.gui

import klab.gui.repl.Colors

/**
 * User: karolis@misiunas.com
 * Date: 12/08/2013
 * Time: 01:38
 */
object Print {

  def apply(a: Any) = System.out.println(Colors.print + a + Colors.end)

  /** consistent with scala notation */
  def println(a: Any) = System.out.println(Colors.print + a + Colors.end)

  // todo: auto logging
  /** System warning for error */
  def error(a: Any) = System.out.println(Colors.error + "Error: " + a + Colors.end)

  /** help print */
  def help(a: Any) = System.out.println(Colors.help + a + Colors.end)

}
