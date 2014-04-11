package klab.gui.infrastructure.show

import klab.gui.Print

/**
 * ## object describing how to use Show()
 *
 * called when Show() without any parameters is invoked
 *
 * User: kmisiunas
 * Date: 19/11/2013
 */
object ShowHelp extends ShowType {

  override val priority: Int = 10

  /** Returns true if this handler can deal with this data type */
  def isType(list: Seq[AnyRef]): Boolean = list.isEmpty

  /** Returns iterator that will be written to a file - a line for each string */
  def show(o: Seq[AnyRef]): Unit = {
    Print.help(
      """
        |## Show(arg1, arg2...)
        |--------------------------
        |Pass at least one argument to the function to get a visualisation of that object.
        |It can be customised with multiple parameters.
      """.stripMargin)
  }
}
