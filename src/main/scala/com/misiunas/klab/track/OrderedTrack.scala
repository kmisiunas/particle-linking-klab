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

  /** finds index of Pos that is closest to specified time - t*/
  def findAtTimeIdx(t: Double): Int = {
    //TODO: ugly implementation and does not behave right at equal separations
    if (timeRange._1 > t || timeRange._2 < t) throw new Exception("Warning: this track does not have position at t="+t)
    if (t == apply(0).t) return 0
    if (t == apply(size-1).t) return size-1
    def search(down: Int, up: Int) : Int = {
      val nr = (up-down)/2 + down
      if (apply(nr).t == t) return nr
      if(up==down) return up
      if(up-down == 1)
        if( Math.abs(t-apply(up).t) >= Math.abs(t-apply(down).t)) return down
        else return up
      if(apply(nr).t > t) return search(nr, down)
      else return search(up, nr)
    }
    def correct(i: Int) : Int = {
      if(i>0 && Math.abs(t-apply(i-1).t) < Math.abs(t-apply(i).t))  return i-1
      else if(i<size-1 && Math.abs(t-apply(i+1).t) < Math.abs(t-apply(i).t))  return i+1
      else return i
    }
    correct(search(0, size-1))
  }

  /** finds Position closes to specified time */
  def findAtTime(t: Double) : Pos = try {
    apply(findAtTimeIdx(t))
  } catch {
    case e:Exception => return null
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
