package klab.track.analysis.specialised

import com.misiunas.geoscala.Point
import klab.track.Track
import breeze.linalg.DenseVector
import klab.track.analysis.Find
import klab.track.geometry.position.Pos
import klab.track.operators.{TwoTracks, Bin}
import klab.track.operators.TwoTracks.PairInteraction
import klab.track.assemblies.TrackAssembly

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

  def twoParticles(along: Point => Double, binSize: Double = 1.0): Iterable[Track] => Map[String,DenseVector[Double]] =
  ta =>{

    val interactions = TwoTracks.findTwoParticleInteractions()(ta)

    val size = 100 // always have 100 bins
    val separation = breeze.linalg.linspace(0, (size-1)*binSize, size) // always have 100 bins

    def analyse(i: PairInteraction): List[(Double, Double)] = {
      def iterate(left: List[(Pos,Pos)], acc: List[(Double, Double)] = Nil): List[(Double, Double)] = {
        if (left.tail.isEmpty) return acc
        if (left(0)._1.t +1 != left(1)._1.t) iterate(left.tail, acc)
        else {
          val jump1 = left(0)._1 - left(1)._1
          val jump2 = left(0)._2 - left(1)._2
          if (along(jump1).signum == along(jump2).signum)
            iterate(left.tail, (along(left(0)._1) - along(left(0)._2), 1.0) :: acc)
          else iterate(left.tail, acc)
        }
      }
      iterate(i.interact)
    }

    def count(i: PairInteraction): List[(Double, Double)] = {
      def iterate(left: List[(Pos,Pos)], acc: List[(Double, Double)] = Nil): List[(Double, Double)] = {
        if (left.tail.isEmpty) return acc
        if (left(0)._1.t + 1 != left(1)._1.t) iterate(left.tail, acc) // ignore if frame is missing
        else iterate(left.tail, (along(left(0)._1) - along(left(0)._2), 1.0) :: acc )
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


  /** one particle jump direction analysis */
  def oneParticle(along: Point => Double, binSize: Double = 1.0): TrackAssembly => Map[String,DenseVector[Double]] =
  ta => {

    // result explained: (Position, 1 for jump forward and 0 for backward jump)
    // skip no jums
    def recursive(list: List[Pos], last: Pos, acc: List[(Double, Double)]): List[(Double, Double)] = {
      if (list.isEmpty) return acc
      if (!list.head.isAccurate || !last.isAccurate) return recursive(list.tail, list.head, acc)
      (along(list.head) - along(last)) match {
        case x if x>0.0 => recursive(list.tail, list.head, (along(list.head), 1.0) :: acc ) // forward
        case x if x<0.0 => recursive(list.tail, list.head, (along(list.head), 0.0) :: acc ) // backward
        case x if x==0.0 => recursive(list.tail, list.head, acc )
      }
    }

    val jumps = ta.toList.flatMap( t => recursive(t.list.tail, t.list.head, Nil))

    val binNumber = ((along(ta.range._2) - along(ta.range._1)) / binSize).abs.ceil.toInt
    val bins = breeze.linalg.linspace(along(ta.range._1), binNumber*binSize, binNumber)

    val forward = Bin(bins, jumps)
    val total = Bin(bins, jumps.map(v => (v._1, 1.0)) )
    val backward = total - forward

    Map("n" -> total,
        "forward" -> forward,
        "backward" -> backward,
        "P(forward)" -> (forward / total),
        "P(backward)" -> (backward / total)
    )
  }

}
