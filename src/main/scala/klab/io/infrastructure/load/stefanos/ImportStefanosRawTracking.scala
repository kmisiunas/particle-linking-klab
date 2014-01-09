package klab.io.infrastructure.load.stefanos

import klab.track.geometry.position.Pos
import klab.io.{Load, Path}
import klab.io.infrastructure.load.TableInterpreter

/**
 * == For importing raw tracing video from directory ==
 *
 * Type of format this can read:
 *
 * selected_dir/../id_trajectory_{x,y}.txt
 *
 * where "id" will indicate the order of the file - not really needed. x or y indicates the coordinate.
 *
 * inside each file there is multiple column for each tracked coordinate: time, x1, x2, x3...
 *
 * if both coordinates are zeros, they should be removed - formatting
 *
 *
 * Created by kmisiunas on 04/01/2014.
 */
object ImportStefanosRawTracking {

  def apply(dir: Path):  List[Pos] = {

    // get all the files
    val filesX = dir.listDeepFiles.filterNot(file => "[0-9]{0,4}_trajectory_x.txt".r.findFirstIn( file.name ).isEmpty )
    val filesY = dir.listDeepFiles.filterNot(file => "[0-9]{0,4}_trajectory_y.txt".r.findFirstIn( file.name ).isEmpty )

    // get content
    val x = filesX.map( Load(_) )
                  .flatMap( _.lines.toList )
                  .map( TableInterpreter.findElem(_).map(_.toDouble) )
                  .sortBy( _.head ) // time frame

    val y = filesY.map( Load(_) )
      .flatMap( _.lines.toList )
      .map( TableInterpreter.findElem(_).map(_.toDouble) )
      .sortBy( _.head ) // time frame

    // safety checks
    if (x.size != y.size) throw new RuntimeException("number of elements x and y elements are different")

    def iterate(leftX: List[Array[Double]],leftY: List[Array[Double]], acc: List[Pos]): List[Pos] = {
      if (leftX.isEmpty) return acc
      iterate(leftX.tail, leftY.tail,  combine(leftX.head, leftY.head) ::: acc)
    }

    def combine(x: Array[Double], y: Array[Double]): List[Pos] = {
      if (x(0) != y(0)) throw new RuntimeException("could not align time in the import")
      if (x.size != y.size) throw new RuntimeException("diffrent nuber of data points for x and y")
      x.zip(y).tail
        .map( c => Pos(x(0), c._1, c._2) )
        .filterNot( p => p.x == 0 && p.y == 0) // zeros are empty elements in the old output
        .toList
    }

    iterate(x,y, Nil).sortBy(_.t)
  }

}
