package klab.io.infrastructure.save

import klab.io.formating.ExportJSON

/**
 *
 * User: kmisiunas
 * Date: 18/11/2013
 */
object SaveJSON extends SaveType {

  override val defaultFileExtension = ".json"
  override val kind: String = "json"
  override val priority: Int = 1

  /** Returns true if this handler can deal with this data type */
  def isType(that: Any): Boolean = that match {
    case x: ExportJSON => true
    case _ => false
  }

  /** Returns iterator that will be written to a file - a line for each string */
  def getWriter(that: Any, path: String): Iterator[String] = that match {
    case x: ExportJSON => Iterator(x.toJSON)
    case _ => throw new UnsupportedOperationException("This version of SaveType can't handle this type")
  }

}
