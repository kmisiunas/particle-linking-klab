package klab.track.geometry

import com.misiunas.geoscala.volumes.{Volume, BoxXY}
import com.misiunas.geoscala.Point

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
                       val geometry: BoxXY, // the channel shape
                       val line: (Point=>Double),// the direction of the channel
                       val binX : Double, // the size of the bins along x
                       val binY: Double,  // same along y
                       val units: List[String])
  extends Volume {

  /** returns true if the particle is in the beginning of the channel (2/10th of the length) */
  def isInBeginning(p: Point): Boolean =
    (line(p) >= middle.x - length/2) && (line(p) <= middle.x - length*(3/10))

  /** returns true if the particle is in the beginning of the channel (2/10th of the length) */
  def isInEnding(p: Point): Boolean =
    (line(p) >= middle.x + length*(3/10)) && (line(p) <= middle.x + length/2)

  /** evaluates the beginning of the channel - only works for channels along x */
  lazy val chBeginning = BoxXY(geometry.min, geometry.range.x/10, geometry.range.y)
  lazy val chEnding = BoxXY(Point(geometry.max.x, geometry.min.y), - geometry.range.x/10, geometry.range.y)

  def isWithin(p: Point): Boolean = geometry.isWithin(p)
  def distance(p: Point): Double = geometry.distance(p)

  def gridX : List[Double] = ???

  lazy val length: Double = geometry.range.x

  /** returns middle of the channel (should this be a function of geometry?) */
  lazy val middle: Point = geometry.min + Point( geometry.range.x/2, geometry.range.y/2)


  override def toString: String = "Channel("+name+")"

  def mkString: String = "Channel from " + geometry.min.x + units(0) +" to " + geometry.max.x + units(0)
}

object Channel {

  /** creates a 1 dimenssional channel along z dirrection
    * @param beginAt channel begins at this position
    * @param endAt channel ends at this position
    */
  def simpleAlongX(beginAt: Double, endAt: Double,
                  name: String = "1D channel along X",
                  units: List[String] = List("px_x","px_y", "px_y")): Channel = {

    val chLength = endAt - beginAt
    val width = 4 * chLength // really wide
    return new Channel(name,
                       BoxXY(Point(beginAt, -width/2), chLength, width),
                       p => p.x,
                       chLength / 10,
                       chLength / 10,
                       units )
  }
}