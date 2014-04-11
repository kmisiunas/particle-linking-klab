package klab.track.infreastructure.tracks

import klab.track.geometry.position.Pos
import klab.track._

/**
 * == Track trait defines what it means to be a track ==
 *
 * Aims:
 *  - define access methods that any abstract track should have
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 14:10
 */
trait TrackBase {

  /** Tracks should be identifiable */
  def id: Int

  /** returns position of that element in the list */
  def apply(i: Int): Pos

  /** returns an ordered list with particle positions */
  def list: List[Pos]

  /** returns the number of elements in this track */
  def size: Int

  def timeRange : TimeRange = (range._1.t, range._2.t)

  /** returns two Pos vectors that bound the particle motion in space time. Equivalent to (min, max) */
  def range : STRange

  def min : Pos = range._1
  def max : Pos = range._2

  /** returns true if there is no position records in this Track */
  def isEmpty : Boolean

}