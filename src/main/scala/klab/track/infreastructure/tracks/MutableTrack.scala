package klab.track.infreastructure.tracks

import klab.track.geometry.position.Pos
import scala.annotation.tailrec

/**
 * == A collection of mutation methods for Tacks ==
 *
 * User: karolis@misiunas.com
 * Date: 19/08/2013
 * Time: 16:01
 */
trait MutableTrack [Self <: MutableTrack[Self]] extends OrderedTrack with TrackInfo {
  this: Self =>

  // ---------- Abstract method declarations ----------

  /** A private constructor method */
  protected def makeFrom( id: Int,
                      list: List[Pos], // form of storage goes here!
                      units: List[String],
                      experiment:String,
                      comment: String,
                      time: Long): Self


  // ------------ New Methods -------------

  /** an actual method for building new elements */
  protected def make(id: Int,
                      list: Iterable[Pos], // form of storage goes here!
                      units: List[String],
                      experiment:String,
                      comment: String,
                      time: Long): Self = {
    // check if there are enough units provided
    if (units.size != 4) throw new RuntimeException("4 units should be provided instead of: "+ units)
    // make a temporary track
    val newTrack = makeFrom(id, list.toList, units, experiment, comment, time)
    if (newTrack.isTimeOrdered) return newTrack
    else return newTrack.timeOrder
  }


  def changeId(newId: Int): Self =
    makeFrom(newId,list,units,experiment,comment,time) // is faster to use makeFrom but not safe!

  def changePositions(newList: List[Pos]): Self =
  make(id,newList,units,experiment,comment,time)

  def changePositions(transform: Pos => Pos): Self =
    make(id,list.map(transform),units,experiment,comment,time)

  def changeComment(newComment: String): Self =
    make(id,list,units,experiment,newComment,time)

  def appendComment(text: String): Self =
    changeComment( if (comment.isEmpty) text else commentSeparator + " " + text)

  def changeUnits(newUnits: List[String], transform: Pos => Pos = p => p ): Self =
    make(id,list.map(transform),newUnits,experiment,comment,time)


  /** orders the Pos list according to their time  - should not be needed if everything was prepared correctly */
  def timeOrder: Self = changePositions(list.sortWith(_.t < _.t))

  /** Update the structure of the ParticleTrack to indicate the quality   */
  def qualityCheck: Self = {
    // time separation - could be better!
    val expectedDT: Double = (timeRange._2 - timeRange._1)/(size-1) * 1.1
    @tailrec
    def searchT(left: List[Pos], acc: List[Pos] = Nil) : List[Pos] = {
      if (left.tail.isEmpty) return (left.head :: acc).reverse
      if (left.tail.head.dT(left.head) > expectedDT) // dT too big
        searchT(left.tail.head.toLQPos :: left.tail.tail, left.head.toLQPos :: acc)
      else if (left.tail.head.dT(left.head) <= 0) { // there are two Pos with the same time - delete one
        searchT(left.tail.head.toLQPos :: left.tail.tail, acc)
      } else {
        searchT(left.tail, left.head :: acc)
      }
    }
    // todo: implement spacial check!
    changePositions(searchT(list))
  }



  // --------- Implemented Methods --------------
}
