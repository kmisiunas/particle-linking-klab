package com.misiunas.klab.track.geometry

import com.misiunas.klab.track.position.Pos

/**
 * == A flattened representation of a plane (line) ==
 *
 * User: karolis@misiunas.com
 * Date: 22/07/2013
 * Time: 17:11
 */
class Plane2D private (val p1: Point, val p2:Point) extends GeoSurface {

  /** returns the closest distance between the point and the geometrical feature */
  def distance(p: Point): Double = {
    // more complex as it is not infinite line => have to check the ends first
    if( ((p - p1) dot (p2 - p1)) <= 0 ) return (p - p1).vectorLength
    if( ((p - p2) dot (p1 - p2)) <= 0 ) return (p - p2).vectorLength
    // point distance to a line
    return (p - p1).vectorLength - ((p - p1) dot (p2 - p1).normalise)
  }

}

object Plane2D {
  def apply(p1:Point, p2:Point) : Plane2D = new Plane2D(p1,p2)
}
