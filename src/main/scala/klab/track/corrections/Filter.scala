package klab.track.corrections

import klab.track.assemblies.{TrackAssembly, Assembly}
import klab.track.ParticleTrack
import klab.track.analysis.Proximity
import com.misiunas.geoscala.volumes.{Everywhere, Volume}
import klab.track.corrections.specialised.Continuum

/**
 * == Custom filters for track assemblies ==
 *
 * User: karolis@misiunas.com
 * Date: 23/07/2013
 * Time: 19:56
 */
object Filter {

  private final val inf = Integer.MAX_VALUE


  /**
   * Filters out particle tracks that do not reach the minimum size or exceed the maximum size (optional)
   * @param min (default 2)
   * @param max (default infinity)
   */
  def bySize[A <: Iterable[ParticleTrack]](min: Int, max: Int = inf): A => A =
    ta => returnSameType(ta)(
      ta.filter(pt => pt.size >= min && pt.size <= max)
    )

  /**
   * Filters out particle tracks that never enter the specified volume element
   * @param inside the region inside which the particles have to be
   */
  def byLocation[A <: Iterable[ParticleTrack]](inside: Volume): A => A  =
    ta => returnSameType(ta)(
      ta.filterNot( _.list.forall(!inside.isWithin(_)) )
    )


  /** Filter out non-continuous tracks in set region */
  def byContinuity[A <: Iterable[ParticleTrack]](within: Volume): A => A  =
  ta => returnSameType(ta)({
    val nc = Continuum.find(within)(ta)
    (ta.toSet &~ (nc._1 ++ nc._2 ++ nc._3))
  })


  /** Filters all tracks that overlap by specified proximity in specified area (expensive: > n^4^ or even n^5^) */
  def byProximity[A <: Iterable[ParticleTrack]](minDistance: Double, within: Volume = Everywhere()): A => A =
  ta => returnSameType(ta)(
     ta.filter(t =>
      Proximity.distances(t)(ta)
        .filter(_.distance < minDistance)
        .filter(a => within.isWithin(a.thisPos) && within.isWithin(a.thatPos))
        .isEmpty
      )
  )


}
