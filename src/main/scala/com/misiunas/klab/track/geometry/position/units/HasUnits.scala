package com.misiunas.klab.track.geometry.position.units

import com.misiunas.klab.track.formating.CompatibleWithJSON

/**
 * A trait for knowing if the collection has units
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 16:17
 */
trait HasUnits {

  def units : List[String]

  // TODO: transformations?

}
