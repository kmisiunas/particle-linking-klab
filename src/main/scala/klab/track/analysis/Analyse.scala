package klab.track.analysis

import com.misiunas.geoscala.Point
import klab.track.ParticleTrack
import breeze.linalg.DenseVector
import klab.track.analysis.infrastructure.JumpDirection

/**
 * == Easy Access class for analysis methods ==
 *
 * Created by kmisiunas on 08/12/2013.
 */
object Analyse {

  def jumpDirection(along: Point => Double, binSize: Double = 1.0) = JumpDirection.matrixForm(along, binSize)

}
