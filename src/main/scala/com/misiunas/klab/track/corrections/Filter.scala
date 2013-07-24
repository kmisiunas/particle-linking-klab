package com.misiunas.klab.track.corrections

import com.misiunas.klab.track.assemblies.Assembly
import com.misiunas.klab.track.geometry.GeoVolume

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
  def bySize(ta: Assembly, min: Int = 2, max: Int = inf) : Assembly =
    ta.remove(ta.filterNot(pt => pt.size >= min && pt.size <= max).toSeq)

  /**
   * Filters out particle tracks that never enter the specified volume element
   * @param inside the region inside which the particles have to be
   */
  def byLocation(ta: Assembly, inside: GeoVolume) : Assembly =
    ta.remove(
      ta.filter(
        _.list.forall(!inside.isWithin(_)) ).toSeq )


}
