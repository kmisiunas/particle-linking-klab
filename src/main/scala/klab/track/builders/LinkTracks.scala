package klab.track.builders

import klab.track.geometry.position.Pos
import klab.track.Track
import breeze.linalg.{DenseVector, DenseMatrix}

/**
 * == Links Tracks that are discontinuous ==
 *
 * It is part of track construction infrastructure
 *
 * Features:
 *  - .simple() algorithm for linking obvious and easy tracks
 *  - .complex() algorithm for linking less well understood tracks
 *
 * Specs:
 *  - insert LQPos into linked gaps
 *
 * Created by kmisiunas on 08/04/2014.
 */
object LinkTracks {

  /** == Links tracks that are separated in provided list ==
    *
    * This algorithm only attempts linking when it is certain that they belong together
    *
    * Method:
    *  - Use fragments method to manipulate the lists
    *  - use matrix to map out joint fitness
    *
    * Fitness function:
    *  - 0 value is never joined
    *  - higher values better
    *
    *  ToDo:
    *   - maximise overall fitness
    *
    * @param maxSeparation - gives max separation specs for each coordinate
    */
  def simple[A <: Iterable[Track]](maxSeparation: Pos): A => A =
  ta => {
    // step 1: create fragment encapsulation
    val fragments: Map[Int,Fragment] = ta.map( Fragment(_) ).zipWithIndex
                                         .map(el => (el._2,el._1)).toMap
    // step 2: define fitness fn, the higher the better
    def fitnessFun(f1: Fragment, f2: Fragment): Double = {
      (f1.head -- f2.end) match {
        case d if d.t < 0 => 0.0
        case d if d.t > maxSeparation.t => 0.0
        case d if d.x > maxSeparation.x => 0.0
        case d if d.y > maxSeparation.y => 0.0
        case d if d.z > maxSeparation.z => 0.0
        case d => 1 / (d.t + d.vectorLength)
        case _ => 0.0
      }
    }
    // step 3: create network
    val network: DenseMatrix[Double] = DenseMatrix.zeros(fragments.size, fragments.size)
    for (y <- 0 until fragments.size)
      for (x <- 0 until fragments.size)
        network(x,y) = fitnessFun(fragments(x), fragments(y))
    // step 4: cherry picking : rows with only one possibility or highest value
    for (x <- 0 until fragments.size){
      val possible = network(::,x).findAll(_ > 0.0).size
      if (possible >= 1) {
        val maxIdx   = maxIdx( network(::,x) )
        uniteFragments(fragments, x, maxIdx)
        network(::,x) :*= 0.0 // remove the elements - no more joints here
      }
    }
    // step 5: clean up: new ids repack and send away
    klab.track.corrections.
      returnSameType(ta)( assignNewIds( fragments.values.map(_.toTrack).toSet, ta ) )
  }



  // ---------- Helper Functions --------------


  /** Links two particle tracks inserting LQPos in between them */
  def linkTwoTracks(t1: Track, t2: Track): Track = {
    val from = t1.last
    val to = t2.head
    val linker = if (to.t - from.t == 1.0) Nil else chainLinkLQPos(from, to)
    t1.copy(id = -1, list = t2.list ::: linker ::: t1.list)
  }

  /** method for giving new ids to tracks that need it */
  private def assignNewIds(list: Iterable[Track], oldIds:Iterable[Track]): List[Track] = {
    var lastId = oldIds.map(_.id).max
    def getNextId(): Int = { lastId = lastId +1; lastId }

    def iterate(list: Iterable[Track], acc: List[Track]): List[Track] = {
      if (list.isEmpty) return acc;
      if (list.head.id == -1) {
        iterate(list.tail, list.head.changeId(getNextId()) :: acc)
      } else
        iterate(list.tail, list.head :: acc)
    }
    iterate(list, Nil)
  }

  /** function unites two fragments and stores them in the map */
  private def uniteFragments(map: Map[Int,Fragment], i1: Int, i2: Int): Unit = ???

  private def maxIdx(v: DenseVector[Double]): Int = {
    var (max, idx) = (v(0), 0)
    for (i <- 1 until v.length)
      if (v(i) > max)
        (max, idx) = (v(i), i)
    return idx
  }

  /** class for navigating between track beginnings/ends easily */
  private object Fragment{ def apply(track: Track): Fragment = new Fragment(track) }
  private class Fragment(var track: Track) {
    lazy val head = track.head
    lazy val end = track.last
    def canFollow(that: Fragment): Boolean = this.head.t > that.end.t
    def difference(that: Fragment): Pos = this.head -- that.end
    def toTrack: Track = ???
  }

  /** linker chain creator for every frame */
  private def chainLinkLQPos(from: Pos, to: Pos): List[Pos] = {
    val frames = (to.t - from.t).toInt
    val step = (to - from) * (1.0 / frames)
    (1 to (frames-1)).map( i => Pos(from.t + i ,from + (step * i)) ).reverse.toList
  }

}
