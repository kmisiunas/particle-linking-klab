package klab.track.operators

import klab.track.ParticleTrack
import klab.track.geometry.position.Pos
import klab.track.analysis.Find

/**
 * Created by kmisiunas on 14/01/2014.
 */
object OneTrack {

  class IndividualTrack(val _1: ParticleTrack, val atTime: List[Pos]){
    def copy(newTimeList:  List[Pos]): IndividualTrack = new IndividualTrack(_1, newTimeList)
    def map(f: List[Pos] => List[Pos] ) = copy( f(atTime) )
    def track = _1
  }


  /** Finding the tracks that are at least a minimum separation from other tracks */
  def findInIsolation( minSeparation: Double = 20): Iterable[ParticleTrack] => List[IndividualTrack] =
  ta => {

    def findWhenTrackIsAlone(thisTrack: ParticleTrack): Option[IndividualTrack] = {
      /* find list of pos where it is away from other tracks */
      def isAway(from: List[ParticleTrack]): List[Pos] = {
        def iterate(mainTrack: List[Pos], others: Vector[List[Pos]], acc: List[Pos]): List[Pos] = {
          if (mainTrack.isEmpty || others.isEmpty) return acc
          val t = mainTrack.head.t
          val alignedT = others.map( l => dropUntilT(t,l) )
          val tails = alignedT.map(_.tail).filterNot(_.isEmpty)
          val distances = alignedT.map( _.head ).map( mainTrack.head.distance(_) )
          if (distances.forall(_>=minSeparation)) // no one close
            iterate(mainTrack.tail, tails, mainTrack.head :: acc)
          else // there is track close
            iterate(mainTrack.tail, tails, acc)
        }
        def dropUntilT(t: Double, list: List[Pos]): List[Pos] = list.head.t match {
          case t1 if t1 < t => dropUntilT(t, list.tail)
          case t1 if t1 == t => list
          case t1 if t1 > t =>  Pos(t, Double.MaxValue) :: list // no point at this pos, insert one far away
        }
        iterate(thisTrack.list, from.toVector.map(_.list), Nil).reverse
      }

      val coexist = Find.coexist( thisTrack )( ta )
      val aloneAt = isAway(coexist.toList)
      aloneAt match {
        case Nil => None
        case x => Some( new IndividualTrack(thisTrack, x) )
      }
    }

    ta.map(findWhenTrackIsAlone(_))
      .filterNot( _.isEmpty )
      .map( _.get ).toList
      .sortBy( _._1.id )
  }


}
