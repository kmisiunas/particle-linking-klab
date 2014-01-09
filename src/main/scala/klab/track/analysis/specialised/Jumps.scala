package klab.track.analysis.specialised

import com.misiunas.geoscala.Point
import klab.track.ParticleTrack
import breeze.linalg.DenseVector
import klab.track.operators.{Bin, TwoTracks}
import klab.track.operators.TwoTracks.PairInteraction
import klab.track.geometry.position.Pos
import scala.collection.immutable.SortedMap

/**
 * == Analyse Jumps ==
 * User: kmisiunas
 * Date: 11/12/2013
 */
object Jumps {


  /** Finds jump magnitude of coexisting tracks at specified time
    *
    * Returns:
    *  - "left" particle jump magnitude
    *  - "right" particle jump magnitude
    *
    * Warning: method not efficient! */
  def twoParticleAt(along: Point => Double, at: Double, binSize: Double = 1.0):
    Iterable[ParticleTrack] => Map[String, DenseVector[Double]] = ta => {

    val interactions = TwoTracks.findTwoParticleInteractions()(ta)

    val bin = breeze.linalg.linspace(at - binSize/2, at + binSize/2, 2) // always have 100 bins

    def distance(p1: Point, p2: Point) = (along(p1) - along(p2)).abs

    def inBin(x: Double): Boolean = x >= bin(0) && x < bin(1)

    def analyse(i: PairInteraction): List[(Double, Double)] = {
      def iterate(left: List[(Pos,Pos)], acc: List[(Double, Double)] = Nil): List[(Double, Double)] = {
        if (left.tail.isEmpty) return acc
        if (left(0)._1.t +1 != left(1)._1.t) return iterate(left.tail, acc)
        if ( inBin( distance(left.head._1, left.head._2) ) ) {
          val jump1 = along(left(0)._1) - along(left(1)._1)
          val jump2 = along(left(0)._2) - along(left(1)._2)
          // put left  particle always as the first element
          if (along(left(0)._1) <= along(left(0)._2))
            iterate(left.tail, (jump1, jump2) :: acc)
          else
            iterate(left.tail, (jump2, jump1) :: acc)
        }
        else iterate(left.tail, acc) // not in the bin
      }
      iterate(i.interact)
    }

    val jumps = interactions.flatMap(analyse(_))

    val left = DenseVector(jumps.unzip._1.toArray)
    val right = DenseVector(jumps.unzip._2.toArray)

    Map(
      "left" -> left,
      "right" -> right,
      "bin" -> bin
    )
  }


}
