package klab.gui.infrastructure.show

import klab.io.infrastructure.save._

/**
 * ## Infrastructure class that allows automatic usage of Show() method
 *
 * Version: 0.1.5
 * User: kmisiunas
 * Date: 18/11/2013
 */
abstract class ShowType {

  /** The priority of this save file - higher number = more important */
  val priority: Int = 2

  /** Returns true if this handler can deal with this data type */
  def isType(that: Seq[AnyRef]): Boolean

  /** Returns iterator that will be written to a file - a line for each string */
  def show(that: Seq[AnyRef]): Unit
}

object ShowType {

  /** Produces a list of known show types.
    *
    * The current implementation is ugly as each new type has to added manually. It should be possible to use macros instead.
    */
  def knownTypes: List[ShowType] = List(
    ShowParticleTrack,
    ShowHelp
    //ShowDenseVector,
    //ShowDenseMatrix
  )

}
