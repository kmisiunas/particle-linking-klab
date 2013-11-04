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
trait OrderedTrack extends Track {

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


  /** finds index of Pos that is closest to specified time (t).
    * The Method returns time interval lower bound, not a closest element idx.
    * Speed: o(n) - can be improved with a map to constant time (memory expensive).*/
  def atTimeIdx(t: Double): Int = {
    if (timeRange._1 > t || timeRange._2 < t) return -1
    if (t == timeRange._2) return size-1
    // simple search by iteration, because using lists makes it iterate anyway!
    list.indexWhere(_.t > t) -1
  }

  /** finds Position closes to specified time */
  def atTime(t: Double): Option[Pos] = {
    val idx = atTimeIdx(t)
    if (idx == -1) Option.empty
    else Option( apply(idx) )
  }

  def atTime(tMin: Double, tMax:Double): List[Pos] = list.slice(atTimeIdx(tMin), atTimeIdx(tMax))
  def atTime(tRange: TimeRange): List[Pos] = atTime(tRange._1, tRange._1)

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
