package klab.io.infrastructure.load

import klab.track.{TrackBuilder, Track}
import klab.io.Load
import klab.track.geometry.position.Pos

/**
 * User: karolis@misiunas.com
 * Date: 20/08/2013
 * Time: 15:40
 */
object ImportParticleTrack {

  def fromTable(file: String, experiment: String, comment: String = "") : Track = {
    // todo try getting id from name
    val build = TrackBuilder()

    Load.open(file).foreach( l =>
      build.append( interpret( TableInterpreter.findElem(l) ) ) )

    def interpret(list: Array[String]): Pos = Pos(list.toList.map(_.toDouble))

    // todo try to infer the experiment name from dir

    build.experiment = experiment // not sure why it is not experiment_=
    build.comment = comment
    build.toParticleTrack
  }

}
