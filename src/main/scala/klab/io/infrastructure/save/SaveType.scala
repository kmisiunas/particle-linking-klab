package klab.io.infrastructure.save

/**
 * ## Abstraction for automatic save detection
 *
 * Inherit and implement this class for Save(...) method to work automatically with data type
 *
 * Version: 0.1.5
 * User: kmisiunas
 * Date: 12/11/2013
 */
abstract class SaveType {

  /** The priority of this save file - higher number = more important */
  val priority: Int = 2
  
  /** Default file extension to fall back to if non was provided */
  val defaultFileExtension: String = ".txt"

  /** specific shorthand for save type */
  val kind: String = ""
  
  /** Returns true if this handler can deal with this data type */
  def isType(that: Any): Boolean
  
  /** Returns iterator that will be written to a file - a line for each string */
  def getWriter(that: Any, path: String): Iterator[String]
}

object SaveType {

  /** Produces a list of known save types.
    *
    * The current implementation is ugly as each new type has to added manually. It should be possible to use macros instead.
    */
  def getAll: List[SaveType] = List(
    SaveBreeze,
    SaveArrayAnyVal,
    SaveString,
    SaveFigure,
    SaveJSON,
    SaveMapStringToBreeze
  )

}