package klab.track.operators

import klab.track.ParticleTrack
import klab.track.geometry.position.Pos
import klab.track.analysis.Find
import scala.annotation.tailrec
import scala._

/**
 * == Set of operations on two particle tracks ==
 *
 * Created by kmisiunas on 08/12/2013.
 */
object TwoTracks {


  /** Align two particle tracks using time component. Only align components that exist in both tracks
    * The algorithm is focused on performance as it is necessary for higher order functions
    * Time o(n_pos)
    * Note: LQPos points are excluded!
    * Note: Used to be under Find.alignTwoTracks */
  def pairUpOverlaps(t1: ParticleTrack, t2: ParticleTrack): List[(Pos, Pos)] = {
    @tailrec
    def findNext(l1: List[Pos], l2: List[Pos], acc: List[(Pos,Pos)]): List[(Pos,Pos)] = {
      if (l1.isEmpty || l2.isEmpty) return acc.reverse
      if (!l1.head.quality || !l2.head.quality) findNext(l1.tail, l2.tail, acc)
      else if (l1.head.t == l2.head.t) findNext(l1.tail, l2.tail, (l1.head, l2.head) :: acc)
      else if (l1.head.t < l2.head.t) findNext(l1.tail, l2, acc)
      else return findNext(l1, l2.tail, acc)
    }
    findNext(t1.list, t2.list, Nil)
  }


  /** Special class for storing two particle interaction data
    * The interact aligned list is time ordered*/
  class PairInteraction(val _1: ParticleTrack, val _2: ParticleTrack, val interact: List[(Pos, Pos)])


  /** Function for finding only two particle interactions */
  def findTwoParticleInteractions(notCloserThan: Double = 20): Iterable[ParticleTrack] => List[PairInteraction] =
    ta => {
      if (ta.size < 2) throw new RuntimeException("JumpDirection.findTwoParticleInteractions must provide at least 2 tracks")

      def findOverlaps(left: Iterable[ParticleTrack], acc:List[PairInteraction] = Nil): List[PairInteraction] = {
        if(left.tail.isEmpty) return acc
        val track = left.head
        val coexist = Find.coexist(track)(left.tail)
        if (coexist.size == 1) { // simple case - only one interacting particle
          findOverlaps( left.tail,
                        new PairInteraction(track, coexist.head,
                                            pairUpOverlaps(track, coexist.head)) :: acc )
        } else { // complex case - there are multiple overlaps
          // plan:
          //  - find all corresponding overlaps
          //  - if unique overlap - include
          //  - if not, only include if all other overlaps are far away
          val overlaps = coexist.map( pairUpOverlaps(track, _) )
          /** this method will be slow as it is repeating computations */
          def isUnique(otherTrack: ParticleTrack, overlap: List[(Pos,Pos)], others: Set[List[(Pos,Pos)]]):
          Option[PairInteraction] = {
            def goThrough(left: List[(Pos,Pos)], acc: List[(Pos,Pos)]): List[(Pos,Pos)] = {
              if (left.isEmpty) return acc.reverse
              val t = left.head._1.t
              val sameT = others.flatMap( _.find( _._1.t == t).toList ) //todo might break?
                                .map( o => (left.head._1.distance(o._2) , left.head._2.distance(o._2)) ) // compute distances
              if (sameT.isEmpty) goThrough( left.tail, left.head :: acc)
              else if ( sameT.forall( d => (d._1 >= notCloserThan && d._2 >= notCloserThan) ) )
                goThrough( left.tail, left.head :: acc)
              else goThrough( left.tail, acc) // discard
            }
            val distinct = goThrough(overlap, Nil)
            if (distinct.isEmpty) None
            else Some( new PairInteraction(track, otherTrack, distinct) )
          }

          val overlapsSet = overlaps.toSet
          findOverlaps( left.tail,
            coexist.zip(overlaps)
              .map( t => isUnique(t._1, t._2, overlapsSet -- Set(t._2) ) )
              .filterNot( _.isEmpty )
              .map( _.get)
              .toList
            ::: acc)
        }
      }

      findOverlaps(ta, Nil)
    }

}
