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
      if (l1.isEmpty || l2.isEmpty) acc.reverse
      else if (l1.head.t == l2.head.t) {
        if (l1.head.isAccurate && l2.head.isAccurate)  findNext(l1.tail, l2.tail, (l1.head, l2.head) :: acc)
        else  findNext(l1.tail, l2.tail, acc)
      }
      else if (l1.head.t < l2.head.t)  findNext(l1.tail, l2, acc)
      else  findNext(l1, l2.tail, acc)
    }
    findNext(t1.list, t2.list, Nil)
  }


  /** Special class for storing two particle interaction data
    * The interact aligned list is time ordered*/
  class PairInteraction(val _1: ParticleTrack, val _2: ParticleTrack, val interact: List[(Pos, Pos)]) {
    def copy(interactNew:  List[(Pos, Pos)]): PairInteraction =
      new PairInteraction(_1, _2, interactNew)
    def map(f: List[(Pos, Pos)] => List[(Pos, Pos)] ) = copy( f(interact) )
  }

  // todo : tests
  // interactions.forall( pi => pi.interact.forall(p => pi._1.list.contains(p._1)))
  // also do:
  // if ( !atRightDistance.forall( _.interact.forall(p => p._1.t == p._2.t) ) )
  //   throw new Exception("time does not match!!")

  /** Function for finding only two particle interactions */
  def findTwoParticleInteractions(notCloserThan: Double = 20): Iterable[ParticleTrack] => List[PairInteraction] =
    ta => {
      if (ta.size < 2) throw new RuntimeException("JumpDirection.findTwoParticleInteractions must provide at least 2 tracks")

      def findOverlaps(left: Iterable[ParticleTrack], acc:List[PairInteraction] = Nil): List[PairInteraction] = {
        if(left.tail.isEmpty) return acc
        val track = left.head

        /** this method will be slow as it is repeating computations */
        def isUnique(otherTrack: ParticleTrack,
                     overlap: List[(Pos,Pos)],
                     others: Set[List[(Pos,Pos)]]): Option[PairInteraction] = {
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

        val coexist: Set[ParticleTrack] = Find.coexist(track)(left.tail)

        coexist.size match {
          case 0 => findOverlaps( left.tail, acc )
          case 1 => findOverlaps( left.tail,
                      new PairInteraction(track, coexist.head, pairUpOverlaps(track, coexist.head)) :: acc )
          case l if l>1 => { // complex case - multiple particle overlaps
            // plan:
            //  - find all corresponding overlaps
            //  - if unique overlap - include
            //  - if not, only include if all other overlaps are far away
            val overlapsZip = coexist.map( t2 => (t2 , pairUpOverlaps(track, t2)) )
            val overlaps = overlapsZip.map(_._2)
            val newPairs = overlapsZip
              .map( t => isUnique(t._1, t._2, overlaps -- Set(t._2) ) )
              .filterNot( _.isEmpty ).map( _.get ).toList
            findOverlaps( left.tail, newPairs ::: acc)
          }
        }
      }

      findOverlaps(ta, Nil)
    }



  // --------- Helper methods ---------

}
