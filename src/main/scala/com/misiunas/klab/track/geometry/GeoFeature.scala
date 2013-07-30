package com.misiunas.klab.track.geometry

import com.misiunas.klab.track.geometry.position.Pos

/**
 * User: karolis@misiunas.com
 * Date: 22/07/2013
 * Time: 16:51
 */
trait GeoFeature {

  /** returns the closest distance between the point and the geometrical feature */
  def distance(p: Point) : Double

}
