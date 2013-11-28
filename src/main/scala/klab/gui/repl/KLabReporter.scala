package klab.gui.repl

import scala.tools.nsc.interpreter.{IMain, ReplReporter}

/**
 * == Formats REPL output ==
 *
 * Design note: this is rather sugar coating way to implement this.
 * It is done this way to avoid changing any REPL code so we would be compatible with future releases
 *
 * Features:
 *  - Exceptions => Error messages
 *  - Save last error in memory
 *  - Save a flag if error happened during last execution
 *  - color resXX: in statements
 *
 * User: kmisiunas
 * Date: 23/11/2013
 */
class KLabReporter(val intp: IMain) extends ReplReporter(intp) {

  override def printMessage(msg: String): Unit = super.printMessage( KLabReporter.formatOutput(msg) )

}

object KLabReporter {

  def formatOutput(msg: String): String = {
    errorColoring(
    exceptionFormatting(
    resColoring(msg)))
  }


  private def resColoring: String => String = msg => {
    if (msg.startsWith("res")) {
      "res[0-9]{1,4}:".r.findFirstIn(msg) match {
        case Some(st) => Colors.res + st + Colors.end + msg.drop(st.size)
        case None => msg
      }
    }
    else msg
  }

  private def exceptionFormatting: String => String = msg => {
    errorOccurred = false // reset flag
    if (msg.startsWith("java.") && msg.contains("Exception")) {
      lastError = Some(msg)
      errorOccurred = true
      val err = ": .*$".r.findFirstIn(msg) match {
        case Some(v) => v
        case None => msg.takeWhile( _ != '\n')
      }
      val place = if (msg.lines.size >= 2) msg.lines.drop(1).next() else "no trace position"
      Colors.error + "Error: "+err +"\n" + "(" + place.trim +")"
    }
    else msg
  }

  private def errorColoring: String => String = msg => {
    if (msg.startsWith("<console>") && msg.contains("error"))
      Colors.error + msg + Colors.end
    else msg
  }
  

  /** returns last exception that was suppressed */
  var lastError: Option[String] = None

  /* true if last printed message had an exception in it */
  var errorOccurred: Boolean = false

}
