package klab.io.infrastructure.save

/**
 * ## Saves string to a file - helper
 *
 * User: kmisiunas
 * Date: 13/11/2013
 */
object SaveString extends SaveType {

  override val defaultFileExtension = ".txt"
  override val kind: String = "txt"
  override val priority: Int = 0

  /** Returns true if this handler can deal with this data type */
  def isType(that: Any): Boolean = that match {
    case x: String => true
    case x: Iterable[String] => true
    case _ => false
  }

  /** Returns iterator that will be written to a file - a line for each string */
  def getWriter(that: Any): Iterator[String] = that match {
    case x: String => Iterator(x)
    case x: Iterable[String] => x.toIterator
    case _ => throw new UnsupportedOperationException("This version of SaveType can't handle this type")
  }

}
