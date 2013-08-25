package com.misiunas.klab.gui.repl

/**
 * User: karolis@misiunas.com
 * Date: 12/08/2013
 * Time: 01:38
 */
object Print {

  private val c = new Colors

  def apply(a: Any) = System.out.println(c.print + a + c.end)

  /** consistent with scala notation */
  def println(a: Any) = System.out.println(c.print + a + c.end)

}
