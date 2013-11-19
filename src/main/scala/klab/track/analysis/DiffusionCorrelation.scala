package klab.track.analysis

import klab.track.ParticleTrack
import breeze.linalg.{DenseVector}
import klab.track.geometry.position.Pos

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
  def matrixForm(spacing: Double, distanceFn: (Pos, Pos) => Double = _.distance(_)):
    Iterable[ParticleTrack] => Map[String, DenseVector[Double]] =
    ta => {
      // 1 - estimate diffusion coefficients
      val d = ta.flatMap( t => Diffusion.di(t.list) )
      // 2 - prepare separation vector
      // there must be a cut-off value for separation, choose 100.
      val size = 100
      val separation = DenseVector.zeros[Double](size)
      Range(0,100).foreach( i => separation.update(i, i*spacing) ) // populate separation vector
      // 3 - find coexisting diffusion entries
//      type D = Diffusion.Diffusion
//      def search(left: List[D], found: List[(D, List[D])] = Nil ): List[(D, List[D])] = {
//        if(left.tail.isEmpty) return found
//        val sameT = left.tail.filter(v => v.pos.t == left.head.pos.t)
//        return search(left.tail, (left.head, sameT) :: found )
//      }
//      val coincide = search(d.toList).filterNot( _._2.isEmpty )
      val coincide = d.map( v1 => (v1, d.filter( v2 => v1.pos.t == v2.pos.t && v1.pos != v2.pos ) ) )  // double counts! - but we need that!
                      .filterNot( _._2.isEmpty )
      // 4 - bin the data
      val n = DenseVector.zeros[Double](size)
      val sumD = List( DenseVector.zeros[Double](size), DenseVector.zeros[Double](size), DenseVector.zeros[Double](size))
      def bin(p1: Pos, p2: Pos): Int = (distanceFn(p1,p2) / spacing).floor.toInt
      coincide.foreach( v => v._2.foreach( p2 => {
        val p1 = v._1
        val theBin = bin(p1.pos,p2.pos)
        n.update(theBin, n(theBin) + 1)
        (1 until 3)
          .foreach( i => sumD(i-1).update(theBin, sumD(i-1)(theBin) + p1.Di(i)) )
      }))
      // 5 - estimate mean
      val meanD = sumD.map( _ :/ n )
      // 6 - estimate std
      var stdD = List( DenseVector.zeros[Double](size), DenseVector.zeros[Double](size), DenseVector.zeros[Double](size))
      coincide.foreach( v => v._2.foreach( p2 => { // todo: used second time, generalise!
        val p1 = v._1
        val theBin = bin(p1.pos,p2.pos)
        (1 until 3)
          .foreach( i => stdD(i-1).update(theBin, stdD(i-1)(theBin) + Math.pow(p1.Di(i) - meanD(i-1)(theBin) , 2) ) )
      }))
      stdD = stdD.map( _ :/ (n + DenseVector.ones[Double](size)) )
      stdD.foreach( breeze.numerics.sqrt.inPlace( _ ) )

      Map("separation" -> separation,
          "axis" -> separation,
          "n" -> n,
          "dx" -> meanD(0),
          "dy" -> meanD(1),
          "dz" -> meanD(2),
          "stdx" -> stdD(0),
          "stdy" -> stdD(1),
          "stdz" -> stdD(2))
  }

}
