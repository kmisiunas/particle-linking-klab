package klab.track.analysis

import klab.track._
import klab.track.geometry.position.Pos
import scala.annotation.tailrec
import com.misiunas.geoscala.volumes.Volume

/**
 * Find various properties in assemblies
 *
 * User: karolis@misiunas.com
 * Date: 31/07/2013
 * Time: 16:45
 */
object Find {

  /** Find tracks that exist at a specified time */
  def atTime(t: Double): PTFind =
    ta => ta.filter(pt => pt.timeRange._1 <= t && pt.timeRange._2 >= t).toSet

  /** Find tracks that exist at a specified time range */
  def atTime(tMin: Double, tMax: Double): PTFind =
    ta => ta.filter(pt =>
      (pt.timeRange._1 <= tMax && pt.timeRange._2 >= tMin)
      || (pt.timeRange._1 <= tMax && pt.timeRange._2 >= tMax)
      || (pt.timeRange._1 <= tMin && pt.timeRange._2 >= tMin)
    ).toSet


  /** Align two particle tracks using time component. Only align components that exist in both tracks
    * The algorithm is focused on performance as it is necessary for higher order functions
    * Time o(n_pos) */
  def alignTwoTracks(t1: ParticleTrack, t2: ParticleTrack): List[(Pos, Pos)] = {
    @tailrec
    def findNext(l1: List[Pos], l2: List[Pos], acc: List[(Pos,Pos)]): List[(Pos,Pos)] = {
      if (l1.isEmpty || l2.isEmpty) return acc.reverse
      if (l1.head.t == l2.head.t) findNext(l1.tail, l2.tail, (l1.head, l2.head) :: acc)
      else if (l1.head.t < l2.head.t) findNext(l1.tail, l2, acc)
      else return findNext(l1, l2.tail, acc)
    }
    findNext(t1.list, t2.list, Nil)
  }

  type TrackSegment = List[Pos]
  /** Find segments of a track that are within specified volume */
  def segmentsWithin(within: Volume): ParticleTrack => List[TrackSegment] =
  pt => {
    @tailrec
    def findSegments(left: List[Pos], acc: List[TrackSegment] = Nil): List[TrackSegment] = {
      if (left.isEmpty) return acc
      if (!within.isWithin(left.head)) findSegments(left.tail, acc)
      else {
        val res = iterateUntilBreak(left)
        findSegments(res._1, res._2 :: acc)
      }
    }

    @tailrec
    def iterateUntilBreak(left: List[Pos], acc: TrackSegment = Nil): (List[Pos], TrackSegment) = {
      if (left.isEmpty) return (left, acc.reverse)
      if (within.isWithin(left.head)) iterateUntilBreak(left.tail, left.head :: acc)
      else (left, acc.reverse)
    }
    findSegments(pt.list)
  }

}
