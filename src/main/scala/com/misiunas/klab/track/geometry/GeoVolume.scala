package com.misiunas.klab.track.geometry

import com.misiunas.klab.track.position._

/**
 * User: karolis@misiunas.com
 * Date: 22/07/2013
 * Time: 16:47
 */
trait GeoVolume extends GeoFeature {

  /** is the point within the volume element? */
  def isWithin(p: Point) : Boolean


}
