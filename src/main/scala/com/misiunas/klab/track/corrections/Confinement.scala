package com.misiunas.klab.track.corrections

import com.misiunas.klab.track.geometry.{Channel, Point}
import com.misiunas.klab.track.ParticleTrack
import com.misiunas.klab.track.analysis.Find
import com.misiunas.klab.track.geometry.position.Pos
import scala.annotation.tailrec

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
                    val atTimes: List[Double]) {
    override def toString: String = "Track " + thisTrack + " overlaps with " + thatTrack +" at times " + atTimes
    override def equals(a: Any): Boolean = a match {
      case r:ResOverlap => r.thisTrack == thisTrack && r.thatTrack == thatTrack && r.atTimes == atTimes
      case _ => false
    }
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
        else recursive(left.tail, new ResOverlap(track, left.head, overlapsAt) :: acc)
      }
    }
    recursive(Find.atTime(track.timeRange._1, track.timeRange._2)(list).toList)
  }


  /** Cuts and joins two tracks at specified times */
  def correctTrack(r: ResOverlap, newIds: (Int, Int)): List[ParticleTrack] = {
    @tailrec
    def recursive(t1: ParticleTrack, t2: ParticleTrack, atTime: List[Double]) : List[ParticleTrack] = {
      if (atTime.isEmpty) return List(t1,t2)
      val idx1 = t1.findAtTimeIdx(atTime.head)
      val idx2 = t2.findAtTimeIdx(atTime.head)
      recursive(
        t1.changePositions(t1.list.take(idx1) ::: t2.list.drop(idx2)),
        t2.changePositions(t2.list.take(idx2) ::: t1.list.drop(idx1)),
        atTime.tail)
    }
    recursive(r.thisTrack.changeId(newIds._1), r.thatTrack.changeId(newIds._2), r.atTimes)
  }


  /** Method for removing overlapping tracks in a channel */
  def autoCorrection(channel: Channel, messages: Boolean = true): PTFilter =
  ta => {
    def println(s: => Any): Unit = if(messages) System.out.println(s.toString) // printing override
    println("Straiting Confinement.autoCorrection:")
    val inside = Filter.byLocation(channel)(ta)
    val outside = ta.toSet -- inside  // will add at the end
    /** id madness - need to know which tracks are new and which are old, use id */
    val taLastID = ta.toList.sortBy(-_.id).head.id
    var lastID: Int = taLastID
    def getNewId(): Int = { lastID = lastID + 1; lastID }
    /** recursive method to avoid strange overlaps */
    @tailrec
    def recursive(left: Set[ParticleTrack], acc: List[ParticleTrack] = Nil): List[ParticleTrack] = {
      if (left.isEmpty) return acc
      val overlaps = findOverlapsForTrack(left.head, left.tail.toList, channel.line)
      if (overlaps.isEmpty) recursive(left.tail, left.head :: acc)
      else recursive(
        left -- Set(overlaps.head.thisTrack, overlaps.head.thatTrack) ++ correctTrack( overlaps.head, (getNewId(), getNewId()) ) ,
        acc)
    }
    val corr = recursive(inside.toSet) // do corrections!
    // now IDs are mess, all new tracks have id>taLastID but they have gaps. Remove gaps
    lastID = taLastID
    val corr2 = corr.map(t => if (t.id>taLastID) t.changeId(getNewId()) else t)
    lazy val justNewTracks = corr2.filter(_.id>taLastID).sortBy(_.id)
    println(" - found " + justNewTracks.size + " overlaps")
    println(" - they were untangled and new tracks IDs are [" + justNewTracks.head.id +" to "+ justNewTracks.last.id + "]" )
    val overlapAfter = Confinement.findOverlaps(channel.line)(corr2)
    println(" - the new tracks had " + overlapAfter.size + " subsequent overlaps")
    overlapAfter.foreach(r => println("   * " + r))
    Continuum.qualityCheck( (outside ++ corr2).toList.sortBy(_.id) )
  }

}