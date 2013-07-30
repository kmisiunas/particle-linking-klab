package com.misiunas.klab.track.geometry

/**
 * User: karolis@misiunas.com
 * Date: 29/07/2013
 * Time: 15:04
 */
class Everywhere private () extends GeoVolume {
  def distance(p: Point): Double = 0.0
  def isWithin(p: Point): Boolean = true
  override def toString: String = "Everywhere(Volume)"
}

object Everywhere {
  def apply() = new Everywhere
}
