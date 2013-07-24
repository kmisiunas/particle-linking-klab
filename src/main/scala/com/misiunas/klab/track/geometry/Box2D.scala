package com.misiunas.klab.track.geometry

import com.misiunas.klab.track.position.Pos

/**
 * == A 2D box representation (Quadrilateral) ==
 *
 * The z direction is assumed to be infinite.
 *
 * User: karolis@misiunas.com
 * Date: 23/07/2013
 * Time: 03:10
 */
class Box2D private (p1:Point, p2: Point, p3:Point, p4:Point) extends GeoVolume{

  def isWithin(p: Point): Boolean = {
    // method: go through all sides and check if projections are positive => within the Quadrilateral
    ( (p - p1) dot (p2 - p1) ) >= 0 &&
    ( (p - p2) dot (p3 - p2) ) >= 0 &&
    ( (p - p3) dot (p4 - p3) ) >= 0 &&
    ( (p - p4) dot (p1 - p4) ) >= 0
  }

  /** returns the closest distance between the point and the geometrical feature */
  def distance(p: Point): Double = {
    if(isWithin(p)) return 0.0
    List(Plane2D(p1,p2).distance(p),
          Plane2D(p2,p3).distance(p),
          Plane2D(p3,p4).distance(p),
          Plane2D(p4,p1).distance(p) ).min
  }

  // --------- Other methods ------
  override def toString :String = "Box2D("+p1+"; "+p2+"; "+p3+"; "+p4+")"


}

object Box2D {
  def apply(p1:Point, p2:Point, p3:Point, p4:Point): Box2D = new Box2D(p1,p2,p3,p4)

  /**
   * Makes a 2D rectangle box from a left bottom corner Point and the width and height
   */
  def apply(p1:Point, width:Double, height:Double): Box2D =
    Box2D(p1 , p1 + Point(width,0) ,p1 + Point(width,height),p1 + Point(0,height))

}