package klab.track.builders

import klab.track.geometry.position.Pos
import klab.track.Track
import klab.gui.Print
import breeze.linalg.DenseMatrix
import klab.track.analysis.Diffusion

import scala.annotation.tailrec


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


  // todo need simplification and modularisation
  /** finds simple connections and makes tracks out of them */
  def simple(safeDistance: Double): Seq[Pos] => List[Track] =
  pos => {
    val atT = segmentWithT(pos)
    val t0 = atT.keys.min
    val tFinal = atT.keys.max
    val range1: Double = safeDistance
    val range2: Double = range1 * 2

    @tailrec
    def analyseFrame(t: Int, tm1: Iterable[List[Pos]], acc: List[List[Pos]]): List[List[Pos]] = {
      if (t > tFinal) return tm1.toList ::: acc
      else if (!atT.contains(t)) return analyseFrame(t+1, Nil, tm1.toList ::: acc ) // missing frame - end tracks
      else if (tm1.isEmpty) return analyseFrame(t+1, atT(t).map(p=>List(p)),  acc ) // nothing from the past - next
      else {

        val loose = atT(t).toSet

        // if tm1(i) has one track in close proximity and all proximity
        //    AND corresponding track only has it in all proximity
        //    => connect
        @tailrec
        def goThroughRows(tm1: Iterable[List[Pos]],
                          tLeft: Set[Pos],
                          connect: Set[List[Pos]],
                          end: Set[List[Pos]]): (Set[List[Pos]], Set[List[Pos]]) = {
          if (tm1.isEmpty) return (tLeft.map(List(_)) ++ connect, end)
          val distances = tLeft.map(p => p -> p.distance(tm1.head.head))
            .filter(_._2 <= range2)
          if (distances.size == 1 && // just one, check if for other places
            (tm1.tail.forall(l => l.head.distance(distances.head._1) > range2)) && // no alternative connection
            (loose - distances.head._1).forall(p => p.distance(distances.head._1) > range2)) // no close points
          {
            goThroughRows(tm1.tail,
              tLeft - distances.head._1,
              connect + (distances.head._1 :: tm1.head),
              end)
          } else {
            // more than one track in large surroundings
            goThroughRows(tm1.tail, tLeft, connect, end + tm1.head)
          }
        }
        val (continue, end) = goThroughRows(tm1, loose, Set(), Set())
        analyseFrame(t + 1, continue, end.toList ::: acc)
      }
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
