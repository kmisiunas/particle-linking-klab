package klab.track.corrections

import klab.track.geometry.Channel
import com.misiunas.geoscala.Point
import klab.track.ParticleTrack
import com.misiunas.geoscala.volumes.{Everywhere, Volume}
import klab.track.corrections.specialised.Confinement
import klab.track.assemblies.TrackAssembly

/**
 * == Function collection for performing track assembly corrections ==
 *
 * Features:
 *  - Try accepting Assemblies and Lists as an input
 *  - Write long code elsewhere, to keep this class universal but accessible
 *
 * Version: 0.1.6
 * User: kmisiunas
 * Date: 26/11/2013
 */
object Correction {


  /** Removes the overlaps form the track in specified volume and along specified line */
  def overlaps(along: Point => Double = _.x, within: Volume = Everywhere()): TrackAssembly => TrackAssembly =
    Confinement.fixOverlaps(along, within, true)

  /** Removes the overlaps form the track in specified channel */
  def overlaps(channel: Channel): TrackAssembly => TrackAssembly =
    Confinement.fixOverlaps(channel.along, channel, true)

}
