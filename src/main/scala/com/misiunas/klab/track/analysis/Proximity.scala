package com.misiunas.klab.track.analysis

import com.misiunas.klab.track.TimeRange
import com.misiunas.klab.track.ParticleTrack
import com.misiunas.klab.track.assemblies.Assembly
import com.misiunas.klab.track.geometry.position.Pos

/**
 * == Analyses proximity of tracks ==
 *
 * User: karolis@misiunas.com
 * Date: 29/07/2013
 * Time: 01:47
 */
object Proximity {

  /** returns a list of particles that coexisted with the given track */
  def find(track: ParticleTrack, ta: Assembly) : Set[ParticleTrack] = {
    val tr = track.timeRange
    def rangeOverlap(t1: TimeRange, t2: TimeRange) : Boolean =
      !((t1._1 > t2._2) || (t1._2 < t2._1 ))
    ta.filter( t => rangeOverlap(t.timeRange, tr) ).filterNot(_==track).toSet
  }

  /** Finds distances at closet proximity between tracks (expensive: > n^3^)
    * Ugly implementation - improvements can be done!
    * @return A list with tuple: (particle track , ( closest proximity , pos at that point ) )
    */
  def distances(track: ParticleTrack, ta: Assembly) : List[(ParticleTrack, (Double, Pos))] = {
    val coexist = find(track, ta).toList
    val tr = track.timeRange
    def findDistance(t: ParticleTrack) : (Double, Pos) = {
      // scan through all the elements in the list
      // for each element find time stamp corresponding time stamp
      // iterate to find the smallest one
      val overlap = t.list.filter(p => p.t >= tr._1 && p.t <= tr._2)
      def itrerate(list : List[Pos], shortest : (Double, Pos)) : (Double, Pos) = {
        if(list.isEmpty) return shortest
        val otherP = track.findAtTime(list.head.t)
        if(otherP == null) return itrerate(list.tail, shortest)
        val distance = otherP.distance(list.head)
        return itrerate(list.tail, if(distance < shortest._1) (distance, list.head) else shortest)
      }
      itrerate(overlap, (Double.MaxValue, overlap.head) )
    }
    coexist.zip(coexist.map(findDistance(_))).sortBy(_._2._1)
  }

}
