package klab.track.analysis

import klab.track.geometry.position.Pos
import com.misiunas.geoscala.vectors.Vec
import com.misiunas.geoscala.Point

/**
 * == Estimate the diffusion coefficient ==
 *
 * User: karolis@misiunas.com
 * Date: 26/07/2013
 * Time: 13:31
 */
object Diffusion {

  /** Low Quality diffusion estimator for simple problems */
  def mean1D(line: Pos => Double): Iterable[Pos] => Double =
  list => {
    // primitive method, todo: update
    def sq(x:Double): Double = x*x
    val packed = list.foldLeft( (Pos(0,0).toLQPos: Pos, 0: Double, 0: Int) )( (pack, thisPos) => {
      // pack._1 is last Pos; pack._2 is accumulator, pack._3 is nu of elements
      val (lastPos, sum, noOfElements) = pack
      if (lastPos.quality && thisPos.quality) {
        val dT = thisPos.dT(lastPos)
        val dX = sq(line(thisPos) - line(lastPos))
        (thisPos, sum + dX/(2*dT), noOfElements + 1)
      } else { // there is LQPos - skip
        (thisPos, sum, noOfElements)
      }
    })
    packed._2 / packed._3 // compute mean
  }

  /** MSD return type is a vector of xyz MSD coefficients and the mean position at which i was estimated */
  class MSD(val msd: Vec, val pos: Pos, val n: Int)


  /** Function that estimates MSD value for a given position */
  def msd(frames: Int = 1): Iterable[Pos] => List[List[MSD]] =
  pt => {

    /** function returns "frames" number of MSD points OR Nil if there was not enough elements */
    def estimatePoint(list: List[Pos]): List[MSD] = {
      // TODO: Make sure that low quality points are not included!
      /** Evaluates MSD from short sequence */
      def evaluate(short: List[Pos]): MSD = {
        val n = short.length
        // MSD calculation
        val msd = short.tail
          .foldRight( (Vec(0), short.head)  )( (pos:Pos, acc:(Vec, Pos)) => {
            val d = pos - acc._2; (acc._1 + Vec(d.x*d.x, d.y*d.y, d.z*d.z) , pos)
          } )._1 * (1.0/(n-1))
        val meanPos: Pos =  short.tail.foldRight( short.head )( (p:Pos, acc:Pos) => acc ++ p ) ** (1.0/n)
        new MSD(msd, meanPos, n)
      }
      val performOn = list.take(frames+1).reverse   // inverse order for performance
      if (!performOn.forall(_.isAccurate)) return Nil    // there inaccurate position
      def iterator( left: List[Pos], acc: List[MSD]): List[MSD] = {
        if (left.tail.isEmpty) acc
        else iterator(left.tail, evaluate(left) :: acc)
      }
      iterator( performOn, Nil ).sortBy(_.n)
    }

    def iterator(left: List[Pos], acc: List[List[MSD]]): List[List[MSD]] = {
      if(left.isEmpty) acc
      else iterator( left.tail, estimatePoint(left) :: acc )
    }

    iterator(pt.toList, Nil).filterNot( _.isEmpty )
  }


  /** Estimates diffusion coefficient.
    *
    * Estimate based on second order model - rough description can be found at:
    * "Saving and Doyle: Static and dynamic errors in particle tracking microrheology. BioPhy. J. 88, (2005)."
    * MSD_i = 2 D_i t + { 2 sigma_i^2^ - 2/3 D_i phi } = 2 D_i t + const.
    * where last term on RHS is treated as displacement in the fit.
    * Technically it does a fit over two points and takes the inclination to get D_i
    */
  def accurateDi(): Iterable[Pos] => List[(Vec, Pos)] =
    pt => {
      // get MSD

    }

}
