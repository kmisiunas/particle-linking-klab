package klab.track.corrections.specialised

import klab.track.geometry.Channel
import klab.track.ParticleTrack
import klab.track.analysis.Find
import klab.track.geometry.position.Pos
import scala.annotation.tailrec
import com.misiunas.geoscala.Point
import klab.track.corrections._
import com.misiunas.geoscala.volumes.{Everywhere, Volume}
import klab.gui.Print
import klab.track.assemblies.TrackAssembly

/**
 * == Restrictions on particle motion ==
 *
 * Mostly confinement to 1D line and corrections for overlapping tracks
 *
 * Known problems:
 *  - algorithm does not fix massive jumps in position when two colloids are merged by tracking routine
 *
 * User: karolis@misiunas.com
 * Date: 07/08/2013
 * Time: 05:18
 */
object Confinement {

  /** Class for storing findOverlaps results */
  class ResOverlap (val thisTrack: ParticleTrack,
                    val thatTrack: ParticleTrack,
                    val atTime: Double) {
    override def toString: String = "Track " + thisTrack + " overlaps with " + thatTrack +" at time " + atTime
    override def equals(a: Any): Boolean = a match {
      case r:ResOverlap => r.thisTrack == thisTrack && r.thatTrack == thatTrack && r.atTime == atTime
      case _ => false
    }
    def _1 = thisTrack
    def _2 = thatTrack
    def atPoint: Point = (thisTrack.atTime(atTime).get + thatTrack.atTime(atTime).get) * 0.5
    def toSet: Set[ParticleTrack] = Set(thisTrack, thatTrack)
  }


  /** Finds overlaps between the tracks.
    * The evaluation is lazy, use .toList to evaluate all elements at once.
    * Time o(n^2^) and o(n_pos^1^).
    * @param line line along which the overlaps are not allowed
    */
  def findOverlaps(line: Point => Double): Iterable[ParticleTrack] => List[ResOverlap] =
  ta => {
    /** recursive method for scanning the each track for overlaps */
    @tailrec
    def recursive(list: List[ParticleTrack], acc: List[ResOverlap] = Nil): List[ResOverlap] = {
      if (list.isEmpty) return acc
      recursive(list.tail, acc ::: findOverlapsForTrack(list.head, list.tail, line))
    }
    recursive(ta.toList)
  }


  /** iterator for finding overlaps with 'track' */
  private def findOverlapsForTrack(track: ParticleTrack, list: List[ParticleTrack], line: (Point => Double)): List[ResOverlap] = {
    @tailrec
    def recursive(left: List[ParticleTrack], acc: List[ResOverlap] = Nil): List[ResOverlap] = {
      if (left.isEmpty) return acc.reverse
      val aligned = Find.alignTwoTracks(track, left.head)
      if (aligned.isEmpty) return recursive(left.tail, acc) // rare, but happens
      else {
        /** determine sign */
        def sign(x: (Pos,Pos)): Int = (line(x._1) - line(x._2)).signum match {
          case 0 => 1   // handle the rare case of equal values
          case d:Int => d
        }
        /** recursive check if the sign stays the same */
        @tailrec
        def checkSign(lastSign: Int, scan: List[(Pos,Pos)], acc: List[Double] = Nil): List[Double] = {
          if (scan.isEmpty) return acc.reverse
          if( sign(scan.head) == lastSign) checkSign( lastSign, scan.tail, acc)
          else checkSign( sign(scan.head), scan.tail, scan.head._1.t :: acc)
        }
        val overlapsAt = checkSign( sign(aligned.head), aligned.tail )
        if (overlapsAt.isEmpty) recursive(left.tail, acc)
        else recursive(left.tail, overlapsAt.map(new ResOverlap(track, left.head, _)) ::: acc)
      }
    }
    recursive(Find.atTime(track.timeRange._1, track.timeRange._2)(list).toList)
  }


  /** Cuts and joins two tracks at specified times.
    * No LQPos introduced here */
  def swapAtOverlap(r: ResOverlap, newIds: (Int, Int)): List[ParticleTrack] = {
    val idx1 = r._1.atTimeIdx(r.atTime)
    val idx2 = r._2.atTimeIdx(r.atTime)
    List(
        r._1.changePositions(r._1.list.take(idx1) ::: r._2.list.drop(idx2)).changeId(newIds._1),
        r._2.changePositions(r._2.list.take(idx2) ::: r._1.list.drop(idx1)).changeId(newIds._2)
    )
  }


  /** Method for removing overlapping tracks in a channel
    *
    * Method description:
    *  - Find tracks that enter the specified region
    *  - Find overlaps between those tracks and discard if they happened outside the region
    *  - fix simple overlaps - two tracks overlapping one or more times
    *  - set-up the linear problem and solve it -- todo!
    *  - Fix the tracks and return
    *
    * @param along the line to fix the overlaps along
    * @param within only fix overlaps that happened within this volume
    * @param log ,if tru they will
    */
  def fixOverlaps(along: Point => Double, within: Volume, log: Boolean): TrackAssembly => TrackAssembly =
  ta => {
    def println(s: => String): Unit = if(log) Print.log("overlaps", s) // printing override
    // new tracks must hve new ids:
    val id = ta.getIdMaker
    // find overlaps
    val inside = Find.enters(within)(ta)
    val overlaps = Find.overlaps(along)(inside)
                      .filter( r => within.isWithin(r.atPoint) )
    println("found " + overlaps.size + "overlapping tracks")
    // find simple overlaps
    val overlapComplexity = overlaps.map( o => (o, {
      val s1 = Set(o._1,o._2)
      overlaps count (o2 => {val s2 = Set(o2._1, o2._2); (s1 & s2).size == 1} )
    } ) )
    // unwind the simple overlaps
    val simpleOverlaps = overlapComplexity.filter(_._2 == 0).map(_._1)
    val simpleNewTracks = simpleOverlaps.flatMap( swapAtOverlap( _ , (id.next(), id.next()) ) )
    println(simpleOverlaps.size + " simple overlaps found and fixed")
    val newTracks = simpleNewTracks
    val correctedTracks = (ta.toSet &~ simpleOverlaps.flatMap(_.toSet).toSet) ++ newTracks
    println(overlapComplexity.filter(_._2 > 0).size + " complex tacks could not be fixed")
    returnSameType[TrackAssembly](ta)(correctedTracks)
  }

}