package klab.track.infreastructure.tracks

import klab.track.geometry.position.Pos
import klab.track._
import scala.annotation.tailrec

/**
 * == Represents a time ordered track ==
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 15:50
 */
trait OrderedTrack extends TrackBase {

  // ---------- Abstract method declarations ----------


  lazy val size: Int = list.size
  def apply(i: Int): Pos = list.apply(i)
  def isEmpty = list.isEmpty

  // ------------ New Methods -------------

  /** returns last position of the particle track (implemented for efficiency) */
  lazy val last: Pos = list.last

  /** beginning of particle track */
  lazy val head: Pos = list.head


  /** checks if list conforms to specs : true if it is good quality.
    * Speed: o(n)*/
  def isTimeOrdered: Boolean =
    list.foldLeft( -Double.MaxValue )( (was, is) => if(is.t >= was) is.t else Double.NaN) != Double.NaN


  /** special implementation that gives very quick access by providing map shortcuts - ugly - tested s*/
   private lazy val quickAccessList: Map[Int, (Int, List[Pos]) ] = {
    val step = 50; // frame
    def collectElements(tNext: Int, idx: Int, left: List[Pos], acc: List[(Int, Int, List[Pos])]): List[(Int, Int, List[Pos])] = {
      if (left.tail.isEmpty && left.head.t == tNext) return (tNext,idx, left) :: acc
      else if (left.tail.isEmpty) return acc
      else if (left.head.t <= tNext && left.tail.head.t > tNext) collectElements(tNext+step, idx+1, left.tail, (tNext,idx, left) :: acc)
      else collectElements(tNext, idx+1, left.tail, acc)
    }
    val t0 = list.head.t.toInt
    val res = collectElements((t0/step+1)*step, 1, list.tail, List( ((t0/step)*step, 0, list) ))
    res.map( v => (v._1 -> (v._2, v._3)) ).toMap
  }

  /** quick find according to time - complexity o(L) - ugly? */
  private def atTimeQuick(t: Int): (Int, Pos) = {
    if (timeRange._1 > t || timeRange._2 < t) return (-1, null)
    val closestBlock = (t / 50) * 50
    def lookFor(idx: Int, left: List[Pos]): (Int, Pos) = {
      if (left.isEmpty) return (-1, null)
      left.head.t match {
        case k if k <= t && t < k+1  =>  (idx, left.head)
        case k if k >= t   =>  (-1, null)
        case _ => lookFor(idx+1, left.tail)
      }
    }
    val idxAndListQA = quickAccessList( closestBlock )
    lookFor( idxAndListQA._1, idxAndListQA._2 ) // hardcoded 50 for block size
  }

  /** finds index of Pos that is at specified time (t) */
  def atTimeIdx(t: Double): Int = atTimeQuick(t.toInt)._1

  /** finds Position closes to specified time */
  def atTime(t: Double): Option[Pos] = atTimeQuick(t.toInt) match {
    case (-1, _) => None
    case c => Option( c._2 )
  }

  def atTime(tMin: Double, tMax:Double): List[Pos] = list.slice(atTimeIdx(tMin), atTimeIdx(tMax)+1)
  def atTime(tRange: TimeRange): List[Pos] = atTime(tRange._1, tRange._1)

  /** returns a Pos that is one frame after this one
    * assuming: that time is in frames */
  def nextFame(p: Pos): Option[Pos] = atTime(p.t + 1)

  /** checks if next frame is good quality - move into helper class */
  def hasNextQualityFrame(p: Pos): Boolean = nextFame(p) match {
    case Some(x) => x.isAccurate
    case _ => false
  }

  /** checks if it has a quality frame at time*/
  def hasQualityFrameAt(t: Double): Boolean = atTime(t) match {
    case Some(x) => x.isAccurate
    case _ => false
  }

  // --------- Implemented Methods --------------


  override lazy val timeRange: TimeRange = (apply(0).t, apply(size-1).t)


  lazy val range: STRange = {
    @tailrec def iterate(list: List[Pos], rng: STRange) : STRange = {
      if(list.isEmpty) return rng
      val minP = Pos(list.head.list.zip(rng._1.list).map((e:(Double,Double)) => Math.min(e._1, e._2)))
      val maxP = Pos(list.head.list.zip(rng._2.list).map((e:(Double,Double)) => Math.max(e._1, e._2)))
      iterate(list.tail, (minP,maxP))
    }
    iterate(list.tail, (list.head, list.head))
  }

}
