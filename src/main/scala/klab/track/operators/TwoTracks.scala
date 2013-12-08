package klab.track.operators

import klab.track.ParticleTrack
import klab.track.geometry.position.Pos
import klab.track.analysis.Find
import scala.annotation.tailrec

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


        }
      }

      ???

    }

}
