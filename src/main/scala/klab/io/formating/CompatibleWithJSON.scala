package klab.track.formating

import klab.io.formating.{ImportJSON, ExportJSON}

/**
 * == to JSON and back! ==
 *
 * JSON representation of the data structure
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 14:24
 */
trait CompatibleWithJSON [Self <: CompatibleWithJSON[Self]] extends ExportJSON with ImportJSON[Self] {
  this: Self =>

  /** constructs back the object from JSON string input. */
  def fromJSON(st: String): Self

}
