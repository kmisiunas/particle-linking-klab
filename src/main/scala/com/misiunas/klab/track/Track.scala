package com.misiunas.klab.track

import com.misiunas.klab.track.position.Pos

/**
 * == Track abstract trait to follow position in space time of an object ==
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 14:10
 */
trait Track {

  /** returns position of that element in the list */
  def apply(i: Int) : Pos

  /** returns the number of elements in this track */
  def size : Int

  def timeRange : TimeRange

  /** returns two Pos vectors that bound the particle motion in space time. Equivalent to (min, max) */
  def range : STRange

  def min : Pos = range._1
  def max : Pos = range._2

  /** returns true if there is no position records in this Track */
  def isEmpty : Boolean

}
