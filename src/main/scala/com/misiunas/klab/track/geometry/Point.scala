package com.misiunas.klab.track.geometry

import com.misiunas.klab.track.geometry.position.Pos
import breeze.linalg.DenseVector
import org.apache.commons.math3.analysis.function.Sqrt

/**
 * == Geometrical point representations ==
 *
 * User: karolis@misiunas.com
 * Date: 23/07/2013
 * Time: 03:11
 */
class Point protected (val x: Double, val y:Double, val z:Double) extends GeoFeature {

  // ---------- Manipulations (immutable vectors) - following Breeze convention -----------

  def + (that: Point) : Point = Point(that.x+x, that.y+y, that.z+z)
  def unary_- : Point = Point(-x,-y,-z)
  def - (that: Point) : Point = this + (-that)
  /** element wise multiplication */
  def :* (that: Point) = Point(that.x*x, that.y*y, that.z*z)
  def *: (d: Double) = Point(d*x, d*y, d*z)
  def * (d: Double) = Point(d*x, d*y, d*z)
  def dot (that: Point) = that.x*x + that.y*y + that.z*z
  def cross (that: Point) = Point(y*that.z - z*that.y, z*that.x - x*that.z, x*that.y - y*that.x)



  // ---------- Computations ----------------

  def distance(that: Point): Double = (this - that).vectorLength

  /** returns number of elements in this vector */
  def length : Int = 3

  lazy val vectorLength = Math.sqrt(x*x + y*y + z*z)

  def normalise : Point  = this * (1/vectorLength)



  // ----------- Other methods --------------

  override def toString = "Point("+x+", "+y+", "+z+")"

  def toVec : DenseVector[Double] = DenseVector(x,y,z)

  override def equals(other: Any): Boolean  = other match {
    case t: Point => t.x == x && t.y == y && t.z == z
    case _ => false
  }

}

object Point {
  def apply(x:Double, y:Double, z:Double) : Point = new Point(x,y,z)
  def apply(x:Double, y:Double) : Point = new Point(x,y,0)
  def apply(x:Double) : Point = new Point(x,0,0)
}
