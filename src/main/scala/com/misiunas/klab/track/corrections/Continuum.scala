package com.misiunas.klab.track.corrections

import com.misiunas.klab.track.assemblies.{TrackAssembly, Assembly}
import com.misiunas.klab.track._
import com.misiunas.klab.track.position.Pos
import com.misiunas.klab.track.geometry.{GeoVolume, GeoSurface}

/**
 * == Object containing functions that stitch an non-continuous particle tracks ==
 *
 * User: karolis@misiunas.com
 * Date: 20/07/2013
 * Time: 23:40
 */
object Continuum {

  /**
   * Type of discontinuity:
   *  - -1 for discontinuity at the beginning (time)
   *  - 0 for discontinuity at the beginning an the end
   *  - 1 for discontinuity at the end
   */
  type Discontinuity = Int

  /** finds elements that are not entering through specified surfaces
    * @return (set containing tracks with wrong beginnings only, with wrong beginnings and ends, with wrong ends)
    */
  def findNonContinuousTracks(ta: Assembly,
                              volume: GeoVolume
                               ) : (Set[ParticleTrack], Set[ParticleTrack], Set[ParticleTrack]) = {
    //TODO: Make it universal
    // find tracks that ends are inside the volume
    val wrongEnd = ta.filter(pt => volume.isWithin(pt.last)).toSet
    // find tracks that ends begin within the volume - they should begin outside
    val wrongBeginning = ta.filter(pt => volume.isWithin(pt.head)).toSet
    // have both wrong beginning and end
    val wrongBandE = wrongEnd & wrongBeginning
    return ( (wrongBeginning &~ wrongBandE), wrongBandE, (wrongEnd &~ wrongBandE) )
  }

  /**
   * Function pairs up unfinished tracks
   * @param setWB set of tracks that are missing beginnings
   * @param setWBE set of tracks that are missing beginnings and endings
   * @param setWE set of tracks that are missing endings
   * @param relativeTimeTolerance number of frames to look missing part of the track
   * @return  a list that specifies which tracks should be combined into single ones
   */
  def pairUp(setWB:Set[ParticleTrack],
             setWBE: Set[ParticleTrack],
             setWE: Set[ParticleTrack],
             relativeTimeTolerance: Double = 6) :  List[List[ParticleTrack]] = {

    /** initial matching starting with the the tracks that missing the endings */
    def findMatchFor(needsEnd: Set[ParticleTrack], needsBeginning: Set[ParticleTrack]):List[List[ParticleTrack]] = {
      if(needsEnd.isEmpty) return Nil
      if(needsBeginning.isEmpty) throw new Exception("Warning: Set contains elements that still need beginning, but there is no ends to match them with")
      val pt : ParticleTrack = needsEnd.head
      val timeTolerance = (pt.timeRange._2-pt.timeRange._1)/pt.size * relativeTimeTolerance
      // TODO: code sensitive to coordinate systems
      // TODO: could include time in ordering if we know typical diffusion constant
      val contTrack = (setWB ++ setWBE)
        .filter(ptThis => {val dt = ptThis.head.dT(pt.last); (dt>0 && dt<timeTolerance)}) // find tracks that begin when this ends in short time interval
        .toList.sortBy[Double](ptThis => pt.last.distance(ptThis.head))  // sort by distance from the end
        .take(1) // take the track with shortest distance
      return (pt :: contTrack) :: findMatchFor(
        needsEnd.tail ++ (contTrack.toSet & setWBE),
        needsBeginning &~ contTrack.toSet)
    }

    /** join up list for joining operations */
    def joinListOfOpperations(list: List[List[ParticleTrack]]): List[List[ParticleTrack]] = {
      def iterator(rem : List[List[ParticleTrack]]) : List[List[ParticleTrack]] = {
        if(rem.isEmpty) return Nil
        val foundComb = rem.tail.filter(rem.head.last == _.head)
        if(foundComb.isEmpty) return rem.head :: iterator(rem.tail)
        else return iterator( (rem.head :+ foundComb.head.last) :: rem.tail.diff(foundComb.head))
      }
      iterator(list.filter(_.size > 1))
    }

    return joinListOfOpperations(findMatchFor(setWE, setWB ++ setWBE))
  }

  def endlessCorrections(list: List[List[ParticleTrack]], setWithoutEnds: Set[ParticleTrack]): List[List[ParticleTrack]] = {
    list.filterNot(l => setWithoutEnds(l.last))
  }

  /**
   * Automatically finds discontinuous tracks and joins them up.
   * Also removes the tracks that could not be made continuous and small sub-tracks that are likely to be noise
   */
  def autoCorrection(ta: Assembly, volume: GeoVolume) : Assembly = {
    println("Straiting Continuum.autoCorrection for Assembly: "+ta)
    val (setWB, setWBE, setWE) = findNonContinuousTracks(ta, volume)
    println(" - found "+(setWB ++ setWBE ++ setWE).size +" non-continuous tracks!")
    val corrections = pairUp(setWB, setWBE, setWE)
    println(" - found "+corrections.map(_.size-1).sum +" corrections")
    val finCorrections = endlessCorrections(corrections, setWBE ++ setWE)
    println(" - of which "+finCorrections.map(_.size-1).sum +" made complete tracks")
    var newIDs: Int = ta.maxBy(_.id).id // current top id
    def getNewID: Int = {newIDs = newIDs+1; newIDs}
    val newTracks = finCorrections.map(m => ParticleTrack(getNewID, m, "ParticleTrack"))
    println(" - thus "+newTracks.size +" new tracks were constructed")
    return ta.remove((setWB ++ setWBE ++ setWE).toList).add(newTracks)
  }

  /** function determines the quality of a track assembly */
  def qualityCheck(ta: Assembly) : Boolean = ???




}
