package klab.track.operators

import klab.track.Track
import klab.track.geometry.position.Pos

/**
 * == Set of common transformations on the track ==
 *
 * question: should id remain the same??! yes for now - need universal id engine
 *
 * Created by kmisiunas on 16/04/2014.
 */
object TransformTrack {

  def takeTimes(range:(Double, Double)): Track => Track =
  track => {
    if (range._1 <= track.timeRange._1 && range._2 >= track.timeRange._2) track // all inclusive
    else {
      def within(p: Pos): Boolean = range._1 <= p.t && range._2 >= p.t
      track.copy( list = track.list.filter(within))
    }
  }

}
