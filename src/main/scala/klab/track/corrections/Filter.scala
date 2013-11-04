package klab.track.corrections

import klab.track.assemblies.{TrackAssembly, Assembly}
import klab.track.ParticleTrack
import klab.track.analysis.Proximity
import com.misiunas.geoscala.volumes.{Everywhere, Volume}

/**
 * == Custom filters for track assemblies ==
 *
 * User: karolis@misiunas.com
 * Date: 23/07/2013
 * Time: 19:56
 */
object Filter {

  final val inf = Integer.MAX_VALUE

  /**
   * Filters out particle tracks that do not reach the minimum size or exceed the maximum size (optional)
   * @param min (default 2)
   * @param max (default infinity)
   */
  def bySize(min: Int, max: Int = inf): PTFilter =
    ta => ta.filter(pt => pt.size >= min && pt.size <= max).toList

  /**
   * Filters out particle tracks that never enter the specified volume element
   * @param inside the region inside which the particles have to be
   */
  def byLocation(inside: Volume): PTFilter =
    ta => ta.filterNot( _.list.forall(!inside.isWithin(_)) ).toList


  /** Filter out non-continuous tracks in set region */
  def byContinuity(within: Volume): PTFilter =
  ta => {
    val nc = Continuum.find(within)(ta)
    (ta.toSet &~ (nc._1 ++ nc._2 ++ nc._3)).toList
  }


  /** Filters all tracks that overlap by specified proximity in specified area (expensive: > n^4^ or even n^5^) */
  def byProximity(minDistance: Double, within: Volume = Everywhere()): PTFilter =
  ta =>
     ta.filter(t =>
      Proximity.distances(t)(ta)
        .filter(_.distance < minDistance)
        .filter(a => within.isWithin(a.thisPos) && within.isWithin(a.thatPos))
        .isEmpty
      ).toList


}
