package com.misiunas.klab.track.geometry

import com.misiunas.klab.track.formating.CompatibleWithJSON
import com.misiunas.klab.track.geometry.position.Pos
import scala.collection.immutable.NumericRange

/**
 * == Representation of a channel ==
 *
 * Currently designed for channels along X. Should be generalised. Ugly implementation!
 *
 * User: karolis@misiunas.com
 * Date: 25/07/2013
 * Time: 21:38
 */
class Channel private (val name: String, // the name of the channel (id)
                       val geometry: Box2D, // the channel shape
                       val line: (Point=>Double),// the direction of the channel
                       val binX : Double, // the size of the bins along x
                       val binY: Double,  // same along y
                       val units: List[String])
  extends GeoVolume with CompatibleWithJSON[Channel] {

  /** returns true if the particle is in the beginning of the channel (2/10th of the length) */
  def isInBeginning(p: Point): Boolean =
    (line(p) >= middle.x - length/2) && (line(p) <= middle.x - length*(3/10))

  /** returns true if the particle is in the beginning of the channel (2/10th of the length) */
  def isInEnding(p: Point): Boolean =
    (line(p) >= middle.x + length*(3/10)) && (line(p) <= middle.x + length/2)

  /** evaluates the beginning of the channel - only works for channels along x */
  lazy val chBeginning = Box2D(geometry.min, geometry.widestX/10, geometry.widestY)
  lazy val chEnding = Box2D(Point(geometry.max.x, geometry.min.y), -geometry.widestX/10, geometry.widestY)

  def isWithin(p: Point): Boolean = geometry.isWithin(p)
  def distance(p: Point): Double = geometry.distance(p)

  def gridX : List[Double] = ???

  lazy val length: Double = geometry.widestX

  /** returns middle of the channel (should this be a function of geometry?) */
  lazy val middle: Point = geometry.min + Point( geometry.widestX/2, geometry.widestY/2)


  // JSON
  def toJSON: String = ???
  def fromJSON(st: String): Channel = ???

  override def toString: String = "Channel("+name+")"

  def mkString: String = "Channel from " + geometry.min.x + units(0) +" to " + geometry.max.x + units(0)
}

object Channel {

  /** creates a 1 dimenssional channel along z dirrection
    * @param beginAt channel begins at this position
    * @param endAt channel ends at this position
    * @param noOfBins number of bins that the channel should have
    */
  def simpleAlongX(beginAt: Double, endAt: Double,
                  name: String = "1D channel along X",
                  units: List[String] = List("px_x","px_y", "px_y")): Channel = {

    val chLength = endAt - beginAt
    val width = 4 * chLength
    return new Channel(name,
                       Box2D(Point(beginAt, -width/2), chLength, width), // really wide
                       p => p.x,
                       chLength / 10,
                       chLength / 10,
                       units )
  }
}