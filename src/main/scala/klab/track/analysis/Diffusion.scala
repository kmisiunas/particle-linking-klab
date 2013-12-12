package klab.track.analysis

import klab.track.geometry.position.Pos
import com.misiunas.geoscala.vectors.Vec
import klab.track.ParticleTrack
import klab.track.operators.TimeOperator

/**
 * == Estimate the diffusion coefficient ==
 *
 * Methods implemented:
 *  - naive - takes first point in MSD to estimate diffusion coefficient
 *  - savingAndDoyle - uses first two points of MSD to get slope and estimates from it.
 *
 * Specification:
 *  - Tolerance to short sequences passed as an input.
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


  /** Diffusion coefficient  packed with the position it was measured at.
    * @param Di vector containing cartesian coordinates of the estimated diffusion coeficint
    * @param pos mean position the coefficient was measured at
    */
  class Diffusion( val Di: Vec, val pos: Pos ) { override def toString = "Diffusion( Di="+Di+" at "+pos }



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
        val nn = short.length - 1
        // MSD calculation => MSD_i(k,n) = Sum_k: (x((k+n)T) - x((k)T)^2 /nn = Sum_k: (x(k) - x0)^2 /nn
        val x0 = short.head
        val dSquared = short.tail.map( _ - x0 )
                                 .map(d => Vec(d.x*d.x, d.y*d.y, d.z*d.z))
        val msd = dSquared.fold(Vec(0,0))( (sum,el) => sum + el ) * (1.0/nn)
        val meanPos: Pos =  short.tail.foldRight( short.head )( (p:Pos, acc:Pos) => acc ++ p ) ** (1.0/(nn+1))
        new MSD(msd, meanPos, nn+1)
      }
      def iterator( left: List[Pos], acc: List[MSD]): List[MSD] = {
        if (left.tail.isEmpty) acc
        else iterator(left.dropRight(1), evaluate(left) :: acc)
      }
      val performOn = list.take(n+1)
      if (!performOn.forall(_.isAccurate)) return Nil         // there inaccurate (LQPos) position -> drop sequence
      if (!TimeOperator.isContinuous(performOn)) return Nil   // if there is frame missing -> drop sequence
      iterator( performOn, Nil ).sortBy(_.n)         // inverse order for performance
    }

    def iterator(left: List[Pos], acc: List[List[MSD]]): List[List[MSD]] = {
      if(left.isEmpty) acc
      else iterator( left.tail, estimatePoint(left) :: acc )
    }

    iterator(pt.toList, Nil).filterNot( _.size < n ).reverse
  }


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
