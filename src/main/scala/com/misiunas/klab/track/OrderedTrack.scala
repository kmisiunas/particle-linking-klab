package com.misiunas.klab.track

import com.misiunas.klab.track.geometry.position.Pos
import com.misiunas.klab.track._

/**
 * == represents a time ordered track ==
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 15:50
 */
trait OrderedTrack extends Track{

  /** returns an ordered list with particle positions */
  def list : List[Pos]

  /** orders the Pos list according to their time  - should not be needed if everything was prepared correctly */
  def timeOrder : OrderedTrack

  /** checks if list conforms to specs : true if it is good quality */
  def qualityCheck : Boolean = list.sortWith(_.t < _.t) == list

  /** finds index of Pos that is closest to specified time - t
    * The Method returns time interval lower bound, not a closest element idx */
  def findAtTimeIdx(t: Double): Int = {
    if (timeRange._1 > t || timeRange._2 < t) return -1
    if (t == timeRange._2) return size-1
    // simple search by iteration, because using lists makes it iterate anyway!
    list.indexWhere(_.t > t) -1
  }

  /** finds Position closes to specified time */
  def findAtTime(t: Double) : Pos = {
    val idx = findAtTimeIdx(t)
    if (idx == -1) return null
    else return apply(idx)
  }

  lazy val timeRange : TimeRange = (apply(0).t, apply(size-1).t)

  lazy val range: STRange = {
    def iterate(list: List[Pos], rng: STRange) : STRange = {
      if(list.isEmpty) return rng
      val minP = Pos(list.head.list.zip(rng._1.list).map((e:(Double,Double)) => Math.min(e._1, e._2)))
      val maxP = Pos(list.head.list.zip(rng._2.list).map((e:(Double,Double)) => Math.max(e._1, e._2)))
      iterate(list.tail, (minP,maxP))
    }
    iterate(list.tail, (list.head, list.head))
  }

}
