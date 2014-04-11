package klab.track.analysis

import klab.track.Track
import breeze.linalg.{DenseVector}
import klab.track.geometry.position.Pos
import klab.track.operators.{Bin, TwoTracks}
import klab.track.operators.TwoTracks.PairInteraction
import com.misiunas.geoscala.vectors.Vec

/**
 * == Finds Diffusion between particles that are separated by some distance ==
 *
 * Features:
 * - Produce relation where Di VS distance between two particles
 *
 * Possible problems:
 *  - now only finds correlations between two particles
 *  - unclear if multiple position diffusion algorithm works well for close proclivities
 *
 * Version 0.1.5
 * User: kmisiunas
 * Date: 15/11/2013
 */
object DiffusionCorrelation {

  /** Find Proclivities in matrix form
    *
    * @param spacing spacing to look at between two points starting at 0
    * @param distanceFn function to use for estimating distance between two points. Default is just vector distance.
    */
  def matrixForm2(spacing: Double, distanceFn: (Pos, Pos) => Double = _.distance(_)):
    Iterable[Track] => Map[String, DenseVector[Double]] =
    ta => {
      val interactions = TwoTracks.findTwoParticleInteractions()(ta)

      val size = 100 // always have 100 bins
      val separation = breeze.linalg.linspace(0, (size-1)*spacing, size) // always have 100 bins

      def analyse(i: PairInteraction): List[(Double, Vec)] = {
        val d1 = Diffusion.di(i.interact.map(_._1))
        val d2 = Diffusion.di(i.interact.map(_._2))

        val distances = d1.zip(d2).map( z => distanceFn(z._1.pos, z._2.pos) )

        distances.zip(d1.map(_.Di)) ::: distances.zip(d2.map(_.Di))
      }

      val res: List[(Double, Vec)] = interactions.flatMap(analyse(_))

      val n =  Bin(separation, res.map( z => (z._1, 1.0) ) )
      val dx = Bin(separation, res.map( z => (z._1, z._2.x) ) ) / n
      val dy = Bin(separation, res.map( z => (z._1, z._2.y) ) ) / n
      val dz = Bin(separation, res.map( z => (z._1, z._2.z) ) ) / n

      Map("separation" -> separation,
        "axis" -> separation,
        "n" -> n,
        "dx" -> dx,
        "dy" -> dy,
        "dz" -> dz
      )
    }
}
