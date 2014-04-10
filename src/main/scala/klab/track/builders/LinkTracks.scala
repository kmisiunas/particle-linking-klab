package klab.track.builders

import klab.track.geometry.position.{LQPos, Pos}
import klab.track.Track
import breeze.linalg.{DenseVector, DenseMatrix}
import scala.xml.dtd.ContentModel._labelT
import scala.collection.mutable
import klab.math.optimise.HungarianAlgorithm

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
    *  - construct a matrix with a cost function linking all the Tracks
    *  - use hungarian algorithm to minimise the costs
    *
    *
    * @param maxSeparation - gives max separation specs for each coordinate
    */
  def hungarian[A <: Iterable[Track]](maxSeparation: Pos): A => A =
  ta => {
    // step 1: create fragment encapsulation
    val map: Map[Int,Track] = ta.zipWithIndex.map(_.swap).toMap
    // step 2: define fitness fn, the higher the better
    def costFun(f1: Track, f2: Track): Double = {
      (f1.head -- f2.last) match {
        case d if d.t < 0 => 0.0
        case d if d.t > maxSeparation.t => 0.0
        case d if d.x.abs > maxSeparation.x => 0.0
        case d if d.y.abs > maxSeparation.y => 0.0
        case d if d.z.abs > maxSeparation.z => 0.0
        case d => - 1000 + ( d.t + d.vectorLength) // todo: improve fitness function!
        case _ => 0.0
      }
    }
    // step 3: create network
    val cost: DenseMatrix[Double] = DenseMatrix.zeros(map.size, map.size)
    for (y <- 0 until map.size ; x <- 0 until map.size)
      cost(x,y) = costFun(map(x), map(y))
    // step 5: Hungarian algorithm
    val links = HungarianAlgorithm( cost )._1.zipWithIndex
    val goodLinks = links.filter( p => cost(p._1,p._2) < 0.0).filter(p => p._1!=p._2)
    // step 6: link tracks
    val newTracks = linkSelectedTracks(map, goodLinks)
    // step 7: clean up: new ids repack and send away
    klab.track.corrections.
      returnSameType(ta)( assignNewIds( newTracks, ta ) )
  }



  // ---------- Helper Functions --------------


  /** Links two particle tracks inserting LQPos in between them */
  def linkTwoTracks(t1: Track, t2: Track): Track = {
    val from = t1.last
    val to = t2.head
    if (from.t >= to.t) throw new Exception("could not link two tracks because times overlap")
    val linker = if (to.t - from.t == 1.0) Nil else chainLinkLQPos(from, to)
    t1.copy(id = -1, list = t1.list ::: linker ::: t2.list)
  }

  /** function for joining the indicated tracks */
  def linkSelectedTracks(rawTracks: Map[Int,Track] ,links:Seq[(Int,Int)]): List[Track] = {
    val linkMap = links.toMap
    val linkMapRev = linkMap.map(_.swap)
    def findFirst(el: Int): Int = if (linkMapRev.contains(el)) findFirst(linkMapRev(el)) else el
    val uniquePaths = links.map( p => findFirst(p._1) ).toSet
    def chainConnect(beginning: Int, accTrack: Track): Track = {
      if (!linkMap.contains(beginning)) return accTrack
      chainConnect(linkMap(beginning) , linkTwoTracks(accTrack, rawTracks(linkMap(beginning))) )
    }
    val newTracks = uniquePaths.map( st => chainConnect(st, rawTracks(st) ) )
    val unusedTracks =
      ((0 until rawTracks.size).toSet -- (links.map(_._1).toSet ++ links.map(_._2).toSet))
      .map( rawTracks(_) )
    (unusedTracks ++ newTracks).toList
  }

  /** method for giving new ids to tracks that need it */
  def assignNewIds(list: Iterable[Track], oldIds:Iterable[Track]): List[Track] = {
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

  /** linker chain creator for every frame */
  def chainLinkLQPos(from: Pos, to: Pos): List[Pos] = {
    val frames = (to.t - from.t).toInt
    val step = (to - from) * (1.0 / frames)
    (1 to (frames-1)).map( i => Pos(from.t + i ,from + (step * i)).toLQPos ).toList
  }

}
