package klab.track.analysis.specialised

import klab.track.Track
import klab.track.analysis.Diffusion
import com.misiunas.geoscala.vectors.Vec

/**
 * Estimates mean diffusion coefficient
 *
 * Created by kmisiunas on 11/04/2014.
 */
object MeanDiffusion {

  /** return mean diffusion coefficient of this track */
  def ofTrack: Track => Vec =
  track => {
    val di = Diffusion.di(track.list).map(d => d.Di)
    di.reduceLeft( _ + _ ) * (1.0 / di.length)
  }

}
