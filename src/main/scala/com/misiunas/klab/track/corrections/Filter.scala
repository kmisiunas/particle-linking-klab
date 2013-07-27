package com.misiunas.klab.track.corrections

import com.misiunas.klab.track.assemblies.{TrackAssembly, Assembly}
import com.misiunas.klab.track.geometry.GeoVolume
import com.misiunas.klab.track.ParticleTrack

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
  def byContinuity(ta: TrackAssembly, within: GeoVolume) : TrackAssembly = {
    val nc = Continuum.findNonContinuousTracks(ta,within)
    ta.remove( (nc._1 ++ nc._2 ++ nc._3).toSeq )
  }


}
