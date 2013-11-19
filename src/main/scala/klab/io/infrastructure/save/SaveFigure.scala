package klab.io.infrastructure.save

import breeze.plot.Figure

/**
 * ## Saves Breeze Visualisation
 *
 * User: kmisiunas
 * Date: 18/11/2013
 */
object SaveFigure extends SaveType {

  override val defaultFileExtension = ".pdf"
  override val kind: String = "png"

  /** Returns true if this handler can deal with this data type */
  def isType(that: Any): Boolean = that match {
    case x: Figure => true
    case _ => false
  }

  /** Returns iterator that will be written to a file - a line for each string */
  def getWriter(that: Any, path: String): Iterator[String] = that match {
    case x: Figure => {x.saveas(path); Iterator.empty }
    case _ => throw new UnsupportedOperationException("This version of SaveType can't handle this type")
  }

}