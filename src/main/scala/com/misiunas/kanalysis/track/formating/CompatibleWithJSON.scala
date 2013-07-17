package com.misiunas.kanalysis.track.formating

/**
 * == to JSON and back! ==
 *
 * JSON representation of the data structure
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 14:24
 */
trait CompatibleWithJSON[T] {

  /**
   * Produces a readable JSON file. Implementation should be modular.
   */
  def toJSON : String

  /** constructs back the object from JSON string input */
  def fromJSON(st: String) : T

}
