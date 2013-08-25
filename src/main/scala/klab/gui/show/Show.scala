package com.misiunas.klab.gui.show

import com.misiunas.klab.track.ParticleTrack
import com.misiunas.klab.track.analysis.PosHistogram

/**
 * == DLS for show methods ==
 *
 * //TODO: Proper DLS implementation?
 *
 * User: karolis@misiunas.com
 * Date: 28/07/2013
 * Time: 00:44
 */
object Show {

  def apply(v : Any) : Unit = v match {
    case pt:ParticleTrack => ShowParticleTrack.show(pt)
    case list:Iterable[ParticleTrack] => ShowParticleTrack.show(list)
    case ph:PosHistogram => ph.show()
    case _ => throw new Exception("No show method imlemented for "+v)
  }

}
