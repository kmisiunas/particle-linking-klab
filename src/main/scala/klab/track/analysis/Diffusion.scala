package klab.track.analysis

import klab.track.geometry.position.Pos
import com.misiunas.geoscala.vectors.Vec
import klab.track.ParticleTrack

/**
 * == Estimate the diffusion coefficient ==
 *
 * Methods implemented:
 *  - naive - takes first point in MSD to estimate diffusion coefficient
 *  - savingAndDoyle - uses first two points of MSD to get slope and estimates from it.
 *
 * This object is of CRYTICAL importance!
 *
 * User: karolis@misiunas.com
 * Date: 26/07/2013
 */
object Diffusion {

  /** Easy access to DiffusionCorrelation object */
  def Correlation = DiffusionCorrelation

  /** Easy access to DiffusionLocal object */
  def Local = DiffusionLocal


  /** Low Quality diffusion estimator for simple problems */
  @deprecated
  def mean1D(line: Pos => Double): Iterable[Pos] => Double =
  list => {
    // TODO: method obsolete?
    // primitive method, todo: update and remove?!
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



  /** MSD return type is a vector of xyz MSD coefficients, the mean position, and number of points used
    *
    * @param msd - the MSD_i for three cartesian coordinates.
    * @param pos - the mean position and time for a given MSD point
    * @param n - the number of frames over which the MSD point was estimated
    */
  class MSD(val msd: Vec, val pos: Pos, val n: Int) { override def toString = "MSD("+msd+" at "+pos+" with n="+n+")"}


  /** Estimates MSDi for a Particle Track
    *
    * MSDi(n,k) = 1/n * Sum_k^n+k^(r_i(k+n) - r_i(k))^2^
    *
    * note: this function iterates over all possible k values
    *
    * note: Pos-ition estimate was made using all intermediate points in n.
    *
    * @param n number of frames to estimate msd with from given frame k
    * @return List over all k containing another list of MSDs with different n => MSD(k)(n+1)
    */
  def msd(n: Int = 1): Iterable[Pos] => List[List[MSD]] =
  pt => {

    /** function returns "frames" number of MSD points OR Nil if there was not enough elements */
    def estimatePoint(list: List[Pos]): List[MSD] = {
      /** Evaluates MSD from short sequence */
      def evaluate(short: List[Pos]): MSD = {
        val length = short.length
        // MSD calculation => MSD_i(k,n) = (x((k+n)T) - x((k)T)^2 ...
        val d = short.last - short.head
        val msd = Vec(d.x*d.x, d.y*d.y, d.z*d.z) * (1.0/(length-1))
        val meanPos: Pos =  short.tail.foldRight( short.head )( (p:Pos, acc:Pos) => acc ++ p ) ** (1.0/length)
        new MSD(msd, meanPos, length)
      }
      def iterator( left: List[Pos], acc: List[MSD]): List[MSD] = {
        if (left.tail.isEmpty) acc
        else iterator(left.tail, evaluate(left) :: acc)
      }
      val performOn = list.take(n+1).reverse   // inverse order for performance
      if (!performOn.forall(_.isAccurate)) return Nil    // there inaccurate (LQPos) position -> drop sequence
      iterator( performOn, Nil ).sortBy(_.n)
    }

    def iterator(left: List[Pos], acc: List[List[MSD]]): List[List[MSD]] = {
      if(left.isEmpty) acc
      else iterator( left.tail, estimatePoint(left) :: acc )
    }

    iterator(pt.toList, Nil).filterNot( _.size < n ).reverse
  }


  /** Diffusion coefficient  packed with the position it was measured at.
    * @param Di vector containing cartesian coordinates of the estimated diffusion coeficint
    * @param pos mean position the coefficient was measured at
    */
  class Diffusion( val Di: Vec, val pos: Pos ) { override def toString = "Diffusion( Di="+Di+" at "+pos }

  
  /** Get diffusion coefficients using the best algorithm */
  def di: Iterable[Pos] => List[Diffusion] = savingAndDoyle_Di


  /** Estimates diffusion coefficient.
    *
    * Estimate based on second order model - rough description can be found at:
    * "Saving and Doyle: Static and dynamic errors in particle tracking microrheology. BioPhy. J. 88, (2005)."
    * MSD_i = 2 D_i t + { 2 sigma_i^2^ - 2/3 D_i phi } = 2 D_i t + const.
    * where last term on RHS is treated as displacement in the fit.
    * Technically it does a fit over two points and takes the inclination to get D_i
    */
  def savingAndDoyle_Di: Iterable[Pos] => List[Diffusion] =
    pt => {
      // get MSD
      val msdVal = msd(2)(pt).filter(_.size >= 2)
      // estimate D
      // TODO: works only for 1 frame differences!As long as msd()  discards bigger differences we should be fine.
      val slope = msdVal.map(p => (p(1).msd - p(0).msd) * 0.5) // /dt (= 1)
      val pos = msdVal.map(p => p(1).pos)
      slope.zip(pos).map(zipped => new Diffusion(zipped._1, zipped._2))
    }

  /** Simple Diffusion coefficient estimator.
    *
    * Estimates using fist MSDi(1,k).
    * Di = MSDi / 2 t
    */
  def naive_Di:  Iterable[Pos] => List[Diffusion] =
  pt => {
    val msdVal = msd(1)(pt).filterNot(_.isEmpty)
    val d = msdVal.map(p => p(0).msd * 0.5) // /dt (= 1)
    val pos = msdVal.map(p => p(0).pos)
    d.zip(pos).map(zipped => new Diffusion(zipped._1, zipped._2))
  }

  /** Get units of diffusion (Ugly implementation) */
  def unitsOfDi(pt: ParticleTrack): List[String] = pt.units.tail.map(unit => unit + "^2 / " + pt.units.head)

}
