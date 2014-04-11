package klab.track.analysis

import com.misiunas.geoscala.Point
import klab.track.Track
import breeze.linalg.DenseVector
import klab.track.analysis.specialised.JumpDirection

/**
 * == Easy Access class for analysis methods ==
 *
 * Created by kmisiunas on 08/12/2013.
 */
object Analyse {

  def JumpDirection = klab.track.analysis.specialised.JumpDirection

  def DiffusionRate = klab.track.analysis.specialised.DiffusionRate

  def Jumps = klab.track.analysis.specialised.Jumps

}
