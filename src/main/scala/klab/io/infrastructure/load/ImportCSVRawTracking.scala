package klab.io.infrastructure.load

import klab.track.geometry.position.Pos
import klab.io.{Load, Path}

/**
 * Created by kmisiunas on 11/03/2014.
 */
object ImportCSVRawTracking {

  def apply(file: Path): List[Pos] =
    Load(file).lines.toList
      .map( TableInterpreter.findElem(_) )
      .map( a => Pos(a.map( s => s.trim.toDouble )) )

}
