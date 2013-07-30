package com.misiunas.klab.track.corrections

import com.misiunas.klab.track.assemblies.{TrackAssembly, Assembly}
import com.misiunas.klab.track.geometry.{Everywhere, GeoVolume}
import com.misiunas.klab.track.ParticleTrack
import com.misiunas.klab.track.analysis.Proximity

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
  def bySize(ta: TrackAssembly, min: Int = 2, max: Int = inf) : TrackAssembly =
    ta.remove(ta.filterNot(pt => pt.size >= min && pt.size <= max).toSeq)


  /**
   * Filters out particle tracks that never enter the specified volume element
   * @param inside the region inside which the particles have to be
   */
  def byLocation(ta: TrackAssembly, inside: GeoVolume) : TrackAssembly =
    ta.remove(
      ta.filter(
        _.list.forall(!inside.isWithin(_)) ).toSeq )


  /** Filter out non-continuous tracks in set region */
  def byContinuity(ta: TrackAssembly, within: GeoVolume = Everywhere()) : TrackAssembly = {
    val nc = Continuum.findNonContinuousTracks(ta,within)
    ta.remove( (nc._1 ++ nc._2 ++ nc._3).toSeq )
  }


  /** Filters all tracks that overlap by specified proximity in specified area (expensive: > n^4^ or even n^5^) */
  def byProximity(ta: TrackAssembly, minDistance: Double, within: GeoVolume = Everywhere()) : TrackAssembly = {
    val overlapping = ta.filterNot(t =>
      Proximity.distances(t, ta).filter(a => within.isWithin(a._2._2)).filter(_._2._1 < minDistance).isEmpty
    )
    // exclude tracks that do not comply with the separation
    ta.remove( overlapping.toList )
  }

}
