package klab.track.analysis

import klab.track._
import klab.track.geometry.position.Pos
import scala.annotation.tailrec
import com.misiunas.geoscala.volumes.Volume
import com.misiunas.geoscala.Point
import klab.track.corrections.specialised.Confinement
import Confinement.ResOverlap
import klab.track.analysis.infrastructure.{JumpDirection, Proximity}
import klab.track.operators.TwoTracks
import klab.track.operators.TwoTracks.PairInteraction

/**
 * Find various properties in assemblies
 *
 * TODO:
 *  - general found result representation: Set or List?
 *  - most finding method from all other places should be ported here
 *
 * User: karolis@misiunas.com
 * Date: 31/07/2013
 */
object Find {

  type PTFind = Iterable[ParticleTrack] => Set[ParticleTrack]

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

  /** Find track overlaps along certain axis */
  def overlaps(line: Point => Double): Iterable[ParticleTrack] => List[ResOverlap] = Confinement.findOverlaps(line)

  /** Finds tracks that entered the volume at some point */
  def enters(volume: Volume): PTFind =
    ta => ta.filterNot( _.list.forall( !volume.isWithin(_) ) ).toSet

  /** Finds tracks that always were in the volume */
  def isWithin(volume: Volume): PTFind =
    ta => ta.filter( _.list.forall( volume.isWithin(_) ) ).toSet

  /** Find tracks that coexist with a given track */
  def coexist(t: ParticleTrack): PTFind = Proximity.find(t)


  /** Find the time stamps at which two tracks overlap
    *
    * Note: Excluded LQPos from the overlaps */
   def timeOverlapBetween(t1: ParticleTrack, t2: ParticleTrack): List[Double] = {
    ??? // do we really want this method?
  }

  /** Finds and aligns two particle interactions */
  def twoParticleInteractions: Iterable[ParticleTrack] => List[PairInteraction] =
    TwoTracks.findTwoParticleInteractions()

}
