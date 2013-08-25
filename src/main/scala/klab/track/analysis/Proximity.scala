package com.misiunas.klab.track.analysis

import com.misiunas.klab.track.TimeRange
import com.misiunas.klab.track.ParticleTrack
import com.misiunas.klab.track.assemblies.Assembly
import com.misiunas.klab.track.geometry.position.{LQPos, Pos}
import com.misiunas.klab.track.geometry.{Everywhere, GeoVolume}
import com.misiunas.klab.track.corrections.Filter
import scala.annotation.tailrec

/**
 * == Analyses proximity of tracks ==
 *
 * User: karolis@misiunas.com
 * Date: 29/07/2013
 * Time: 01:47
 */
object Proximity {

  /** returns a list of particles that coexisted with the given track */
  def find(track: ParticleTrack): PTFind =
    ta => Find.atTime(track.timeRange._1, track.timeRange._2)(ta).filterNot(_==track).toSet


  /** class for representing Proximity.distances results
    * @param ptThis particle track 1
    * @param ptThat particle track 2
    * @param distance closes distance between the two
    * @param thisPos Point at which they are closest in track 1
    * @param thatPos Point at which they are closest in track 2
    */
  class ResDistances(val ptThis: ParticleTrack, val ptThat: ParticleTrack, val distance: Double, val thisPos: Pos, val thatPos: Pos) {
    override def toString: String = "ResDistances: between "+ptThis+" and "+ptThat+" the distance is "+distance
  }


  /** Finds distances at closet proximity between tracks (expensive: > n^3^)
    * Ugly implementation - improvements can be done!
    *
    * Plan:
    *  - for each track:
    *  -  find overlapping tracks
    *  -  find minimal distances between them and store them
    *
    * @return A list with ResDistances
    */
  def distances(track: ParticleTrack) : Iterable[ParticleTrack] => List[ResDistances] =
  ta => {
    val coexist: List[ParticleTrack] = Proximity.find(track)(ta).toList
    /** find distance between track and specified track */
    def findDistance(t: ParticleTrack): ResDistances = {
      // scan through all the elements looking for shortest distance
      val overlap = Find.alignTwoTracks(track, t) // only elements that coexist
      if (overlap.isEmpty) return null // tmp fix, todo replace with Option
      val d = overlap.sortBy(tupleP => tupleP._1.distance(tupleP._2)).head
      return new ResDistances(track, t, d._1.distance(d._2), d._1, d._2)
    }
    coexist.map(findDistance(_)).filterNot(_ == null).sortBy(_.distance)
  }

}
