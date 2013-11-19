package klab.gui

import klab.track.ParticleTrack
import klab.track.analysis.PosHistogram
import klab.gui.windows.ShowParticleTrack
import klab.io.infrastructure.save.SaveType
import klab.gui.infrastructure.show.{ShowHelp, ShowType}

/**
 * == Quick show method ==
 *
 * Automatically recognises supplied type and attempts to display it displayed
 *
 * Version 0.1.5
 * User: karolis@misiunas.com
 * Date: 28/07/2013
 */
object Show {

  def apply(v: AnyRef*): Unit = {
    if (v.isEmpty) return ShowHelp.show(v)
    val handlers = ShowType.knownTypes
      .sortBy( - _.priority)
      .filter(_.isType( v ))
    if (handlers.isEmpty) Print.error("no known handler for type: " + v.head.getClass)
    else handlers.head.show( v )
  }

}
