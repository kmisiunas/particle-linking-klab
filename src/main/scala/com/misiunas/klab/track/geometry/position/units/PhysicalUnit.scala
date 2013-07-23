package com.misiunas.klab.track.units

import com.misiunas.klab.track.formating.CompatibleWithJSON

/**
 * == PhysicalUnit representation and implementation ==
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 14:13
 */
class PhysicalUnit extends CompatibleWithJSON[PhysicalUnit]{

  /**  Produces a readable JSON file. Implementation should be modular.   */
  def toJSON: String = ???

  /** constructs back the object from JSON string input */
  def fromJSON(st: String): PhysicalUnit = ???
}
