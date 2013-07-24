package com.misiunas.klab.track.formating

/**
 * User: karolis@misiunas.com
 * Date: 24/07/2013
 * Time: 15:47
 */
trait CompatibleWithCSV[T] {

  val cvsSeparator = ", "

  /**
   * Produces a readable CVS file. Implementation should be modular.
   */
  def toCVS : String

  /** constructs back the object from JSON string input */
  // def fromCVS(st: String) : T

}
