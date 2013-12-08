package klab.track.analysis.infrastructure

import com.misiunas.geoscala.Point
import klab.track.ParticleTrack
import breeze.linalg.DenseVector
import klab.track.analysis.Find
import klab.track.geometry.position.Pos
import klab.track.operators.{TwoTracks, Bin}
import klab.track.operators.TwoTracks.PairInteraction

/**
 * == Finds the jum direction relative to another jump ==
 *
 * The Idea is to find direction at which the particle pair jumped to within the channel. This should be relative
 * to another particle.
 *
 * Technical notes:
 *  - Exclude three particle interactions
 *
 * Created by kmisiunas on 08/12/2013.
 */
object JumpDirection {

  def matrixForm(along: Point => Double, binSize: Double = 1.0): Iterable[ParticleTrack] => Map[String,DenseVector[Double]] =
  ta =>{

    val interactions = TwoTracks.findTwoParticleInteractions()(ta)

    val size = 100 // always have 100 bins
    val separation = breeze.linalg.linspace(0, (size-1)*binSize, size) // always have 100 bins

    def analyse(i: PairInteraction): List[(Double, Double)] = {
      def iterate(left: List[(Pos,Pos)], acc: List[(Double, Double)] = Nil): List[(Double, Double)] = {
        if (left.tail.isEmpty) return acc
        if (left(0)._1.t != left(1)._1.t) iterate(left.tail, acc)
        else {
          val jump1 = left(0)._1 - left(1)._1
          val jump2 = left(0)._2 - left(1)._2
          if (along(jump1).signum == along(jump2).signum) iterate(left.tail, (left(0)._1.t, 1.0) :: acc)
          else iterate(left.tail, acc)
        }
      }
      iterate(i.interact)
    }

    def count(i: PairInteraction): List[(Double, Double)] = {
      def iterate(left: List[(Pos,Pos)], acc: List[(Double, Double)] = Nil): List[(Double, Double)] = {
        if (left.tail.isEmpty) return acc
        if (left(0)._1.t != left(1)._1.t) iterate(left.tail, acc)
        else iterate(left.tail, (left(0)._1.t, 1.0) :: acc )
      }
      iterate(i.interact)
    }

    val nSame = Bin(separation, interactions.flatMap(analyse(_)))
    val n = Bin(separation, interactions.flatMap(count(_)))
    val pSame = nSame / n
    val pOpposite = pSame.map(1 - _)

    Map("separation" -> separation,
        "P(same)" -> pSame,
        "P(opposite)" -> pOpposite,
        "n" -> n)
  }

}
