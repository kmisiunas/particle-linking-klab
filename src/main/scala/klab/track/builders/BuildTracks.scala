package klab.track.builders

import klab.track.geometry.position.Pos
import klab.track.Track
import klab.gui.Print
import breeze.linalg.DenseMatrix
import klab.track.analysis.Diffusion


/**
 * == Constructs Particle Tracks from a list of Pos ==
 *
 * Use track.m as a basis for this tracking routine
 *
 * Code Aims:
 *  - modular system with focus on clarity
 *  - expandability - will want to use same code for when we have features in our particles.
 *  - be prepared for tracking errors - missing particles or ones that exist without particle
 *
 * Guidelines:
 *  - Once done, id tracks in order of first appearance.
 *  - maximumJump is a parameter that measures maximum displacement to consider it as the same track
 *  - Do not bother connecting broken tracks - this will be done by other code
 *  - Do not bother looking for overlaps - it will be done by other code
 *
 * Method:
 *  - Make a distinction between two cases: trivial and complex
 *    - Trivial cases should be the ones where there is only one particle following another
 *    - Trivial cases should be connected straight to Segments
 *  - Segments are collections of Pos that have been lined together they will become tracks.
 *    - Complex cases should be resolved by listing all possible connections and selecting the one with lowest costing function
 *  - CostingFunction should be estimated by diffusive likelihood - distance
 *
 *
 *  [[http://www.mathworks.com/matlabcentral/fileexchange/34040-simple-tracker Simple Tracker by Jean-Yves Tinevez]].
 * Comments:
 *
 * [[http://en.wikipedia.org/wiki/Hungarian_algorithm Hungarian algorithm]] is used to link up particle
 * tracks frame by frame. It can be solved in o(x^3^) -
 * [[https://code.google.com/p/simulation-csx210/source/browse/trunk/src/scalation/maxima/Hungarian.scala?spec=svn81&r=81 scala implementation]]
 * . This will be important when tracking the particles outside the channel. Another implementation:
 * [[https://github.com/KevinStern/software-and-algorithms/blob/master/src/main/java/blogspot/software_and_algorithms/stern_library/optimization/HungarianAlgorithm.java]]
 *
 *
 * Created by kmisiunas on 03/01/2014.
 */
object BuildTracks {

  /** connects Pos into tracks */
  def fromPos(maxSeparation: Pos, safeDistance: Double = 2.0): Seq[Pos] => List[Track] =
  pos => {
    Print.log("info", "Loaded "+pos.length+" positions. Performing Simple connections.")
    val first = simple(safeDistance)(pos)
    Print.log("info", "Done. Now there are "+first.length+" tracks. Performing Hungarian algorithm.")
    val second = LinkTracks.hungarian(maxSeparation)(first)
    Print.log("info", "Done. Finally there are "+second.length+" tracks. ")
    //checkTrackQuality(second, safeDistance)
    LinkTracks.assignNewIds( second, second )
  }


  /** finds simple connections and makes tracks out of them */
  private def simple(safeDistance: Double): Seq[Pos] => List[Track] =
  pos => {
    // ##
    val atT = segmentWithT(pos)
    val t0 = atT.keys.min
    val tFinal = atT.keys.max

    def analyseFrame(t: Int, tm1: List[List[Pos]], acc: List[List[Pos]]): List[List[Pos]] = {
      if (t > tFinal) return tm1 ::: acc
      if (!atT.contains(t)) return analyseFrame(t+1, Nil, tm1 ::: acc )
      if (tm1.isEmpty) return analyseFrame(t+1, atT(t).map(p=>List(p)), tm1 ::: acc )
      val distances: DenseMatrix[Double] = distancesMatrix(tm1, atT(t))
      // what to keep what to send firther
      var continued: List[List[Pos]] = Nil
      var ended: List[List[Pos]] = Nil

      // wrong
      def satisfiesConditions(r: Int, c:Int): Boolean = {
        distances(::, c).findAll(_ <= safeDistance).size == 1 &&
        distances(::, c).findAll(_ <= 3*safeDistance).size == 1 &&
        distances(r, ::).t.findAll(_ <= safeDistance).size == 1 &&
        distances(r, ::).t.findAll(_ <= 3*safeDistance).size == 1
      }

      // ugly - go through rows and columns to check whch overlap
      for (r <- 0 until distances.rows){ // rows for t-1
        val cs = 0 until distances.cols
        val toLink = cs.map(c => satisfiesConditions(r,c))
        toLink.count(x=>x) match {
          case 0 => ended = tm1(r) :: ended
          case 1 => continued = (atT(t)(toLink.indexWhere(b=>b)) :: tm1(r)) :: continued
          case _ => ended = tm1(r) :: ended
        }
      }
      for (c <- 0 until distances.cols){ // columns for t0
        val rs = 0 until distances.rows
        val toLink = rs.map(r => satisfiesConditions(r,c))
        toLink.count(x=>x) match {
          case 0 => ended = List(atT(t)(c)) :: continued
          case 1 => ; // already accounted for
          case _ => ended = List(atT(t)(c)) :: ended // too comlex for this algorithm
        }
      }

      analyseFrame(t+1, continued, ended ::: acc)
    }

    makeTracks( analyseFrame(t0, Nil, Nil) )
  }


  /** separates Pos list according to time */
  private def segmentWithT(list: Seq[Pos]): Map[Int, List[Pos]]= list.toList.groupBy(_.t.toInt)


  /** computes distances matrix between positions */
  private def distancesMatrix(t0: List[List[Pos]], t1: List[Pos]): DenseMatrix[Double] = {
    val mat = DenseMatrix.zeros[Double](t0.length, t1.length)
    for (r <- 0 until mat.rows; c <- 0 until mat.cols){
      mat(r,c) = t0(r).head.distance( t1(c) )
    }
    mat
  }

  /** constructs tracks */
  private def makeTracks(set: List[List[Pos]]): List[Track] = set.map(list => Track(-1,list.sortBy(_.t)))

  /** checks if track complies with expected distance by sampling it */
  def checkTrackQuality(tracks: List[Track], safeDistance: Double): Unit = {
    // sample tracks
    def getRnd(): Int = (tracks.length * math.random).toInt
    val els = List(getRnd(), getRnd(), getRnd()).map(i => tracks(i))
    val meanD = els.map( Diffusion.Mean.ofTrack ).map( v => v.x + v.y + v.z ).sum / 3
    val meanStep = math.sqrt( 2*meanD )
    Print.log("info", "Sampled diffusion was coef is "+meanD.toFloat+ "(px^2/frame), typical step "+meanStep+" (px)")
  }
}
