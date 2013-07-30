package com.misiunas.klab.track.corrections

import com.misiunas.klab.track.assemblies.{TrackAssembly, Assembly}
import com.misiunas.klab.track._
import com.misiunas.klab.track.geometry.position.Pos
import com.misiunas.klab.track.geometry.{Point, GeoVolume, GeoSurface}

/**
 * == Object containing functions that stitch an non-continuous particle tracks ==
 *
 * Continuum algorithm takes inspiration from
 * [[http://www.mathworks.com/matlabcentral/fileexchange/34040-simple-tracker Simple Tracker by Jean-Yves Tinevez]].
 * Comments:
 *
 * [[http://en.wikipedia.org/wiki/Hungarian_algorithm Hungarian algorithm]] is used to link up particle
 * tracks frame by frame. It can be solved in o(x^3^) -
 * [[https://code.google.com/p/simulation-csx210/source/browse/trunk/src/scalation/maxima/Hungarian.scala?spec=svn81&r=81 scala implementation]]
 * . This will be important when tracking the particles outside the channel. Another implementation:
 * [[https://github.com/KevinStern/software-and-algorithms/blob/master/src/main/java/blogspot/software_and_algorithms/stern_library/optimization/HungarianAlgorithm.java]]
 *
 *  Then they join up gaps by joining tracks that are closest.
 *
 *  The algorithm might be further improved if we account for probabilistic distribution for the colloid to diffuse to a given position.
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

    /** estimate time range in which to look for match (could be much faster, now ~ 4n) */
    val timeTolerance = {
      val l = setWE.map(t => (t.timeRange._2-t.timeRange._1)/t.size)
      l.sum / l.size * relativeTimeTolerance
    }

    /** initial matching starting with the the tracks that missing the endings
      *
      * implementation by distance
      * fnSeparation = ptThis => pt.last.distance(ptThis.head))
      *
      * current implementation takes into account to time diffrence based on diffusion coeficient analysis
      * D = L^2^/t
      */
    def findMatchFor(needsEnd: Set[ParticleTrack], needsBeginning: Set[ParticleTrack]) : List[List[ParticleTrack]] = {
      if(needsEnd.isEmpty) return Nil
      if(needsBeginning.isEmpty) throw new Exception("Warning: Set contains elements that still need beginning, but there is no ends to match them with")
      val pt : ParticleTrack = needsEnd.head
      // TODO: code sensitive to coordinate systems
      val fnSeparation : (ParticleTrack => Double) = ptThis => pt.last.distance(ptThis.head)
      val contTrack = needsBeginning
        .filter(t => {val dt = t.head.dT(pt.last); (dt>0 && dt<timeTolerance)}) // find tracks that begin when this ends in short time interval
        .toList.sortBy[Double](fnSeparation)  // sort by distance from the end
        .take(1) // take the track with shortest distance
      return (pt :: contTrack) :: findMatchFor(
        needsEnd.tail ++ (contTrack.toSet & setWBE),
        needsBeginning &~ contTrack.toSet)
    }

    /** join up list for joining operations */
    def joinListOfOperations(list: List[List[ParticleTrack]]): List[List[ParticleTrack]] = {
      def iterator(rem : List[List[ParticleTrack]]) : List[List[ParticleTrack]] = {
        if(rem.isEmpty) return Nil
        val foundComb = rem.tail.filter(rem.head.last == _.head)
        if(foundComb.isEmpty) return rem.head :: iterator(rem.tail)
        else return iterator( (rem.head ::: foundComb.head.tail) :: rem.tail.diff(List(foundComb.head)) )
      }
      iterator(list.filter(_.size > 1))
    }

    return joinListOfOperations(findMatchFor(setWE, setWB ++ setWBE))
  }

  def endlessCorrections(list: List[List[ParticleTrack]], setWithoutEnds: Set[ParticleTrack]): List[List[ParticleTrack]] = {
    list.filterNot(l => setWithoutEnds(l.last))
  }

  /**
   * Automatically finds discontinuous tracks and joins them up.
   * Also removes the tracks that could not be made continuous and small sub-tracks that are likely to be noise
   */
  def autoCorrection(ta: TrackAssembly, volume: GeoVolume) : TrackAssembly = {
    println("Straiting Continuum.autoCorrection for Assembly: "+ta)
    val taF = Filter.bySize(ta, 3)
    println(" - found "+taF.size+" tracks after applying a bySize(>=3) filter")
    val (setWB, setWBE, setWE) = findNonContinuousTracks(taF, volume)
    println(" - found "+(setWB ++ setWBE ++ setWE).size +" non-continuous tracks!")
    val corrections = pairUp(setWB, setWBE, setWE)
    println(" - found "+corrections.map(_.size-1).sum +" corrections")
    val finCorrections = endlessCorrections(corrections, setWBE ++ setWE)
    println(" - of which "+finCorrections.map(_.size-1).sum +" made complete tracks")
    var newIDs: Int = ta.maxBy(_.id).id // current top id
    def getNewID: Int = {newIDs = newIDs+1; newIDs}
    val newTracks = finCorrections.map(m => ParticleTrack(getNewID, m, "ParticleTrack"))
    println(" - thus "+newTracks.size +" new tracks were constructed")
    return taF.remove((setWB ++ setWBE ++ setWE).toList).add(newTracks)
  }

  /** function determines the quality of a track assembly */
  def qualityCheck(ta: Assembly) : Boolean = ???


  /**
   * Function pairs up unfinished tracks and does not allow passes along X
   * @param setWB set of tracks that are missing beginnings
   * @param setWBE set of tracks that are missing beginnings and endings
   * @param setWE set of tracks that are missing endings
   * @param relativeTimeTolerance number of frames to look missing part of the track
   * @return  a list that specifies which tracks should be combined into single ones
   */
  def pairUpNoPass(setWB:Set[ParticleTrack],
                    setWBE: Set[ParticleTrack],
                    setWE: Set[ParticleTrack],
                    relativeTimeTolerance: Double = 6,
                    along : (Point => Double) = _.x) :  List[List[ParticleTrack]] = {

    /** estimate time range in which to look for match (could be much faster, now ~ 4n) */
    val timeTolerance = {
      val l = setWE.map(t => (t.timeRange._2-t.timeRange._1)/t.size)
      l.sum / l.size * relativeTimeTolerance
    }

    /** initial matching starting with the the tracks that missing the endings
      *
      * implementation by distance
      * fnSeparation = ptThis => pt.last.distance(ptThis.head))
      *
      * current implementation takes into account to time diffrence based on diffusion coeficient analysis
      * D = L^2^/t
      */
    def findMatchFor(needsEnd: Set[ParticleTrack], needsBeginning: Set[ParticleTrack]) : List[List[ParticleTrack]] = {
      if(needsEnd.isEmpty) return Nil
      if(needsBeginning.isEmpty) throw new Exception("Warning: Set contains elements that still need beginning, but there is no ends to match them with")
      val pt : ParticleTrack = needsEnd.head
      // TODO: code sensitive to coordinate systems
      val fnSeparation : (ParticleTrack => Double) = ptThis => pt.last.distance(ptThis.head)
      val contTrack = needsBeginning
        .filter(t => {val dt = t.head.dT(pt.last); (dt>(-timeTolerance/2) && dt<timeTolerance)}) // find tracks that begin when this ends in short time interval
        .toList.sortBy[Double](fnSeparation)  // sort by distance from the end
        .take(1) // take the track with shortest distance
      return (pt :: contTrack) :: findMatchFor(
        needsEnd.tail ++ (contTrack.toSet & setWBE),
        needsBeginning &~ contTrack.toSet)
    }

    /** join up list for joining operations */
    def joinListOfOperations(list: List[List[ParticleTrack]]): List[List[ParticleTrack]] = {
      def iterator(rem : List[List[ParticleTrack]]) : List[List[ParticleTrack]] = {
        if(rem.isEmpty) return Nil
        val foundComb = rem.tail.filter(rem.head.last == _.head)
        if(foundComb.isEmpty) return rem.head :: iterator(rem.tail)
        else return iterator( (rem.head ::: foundComb.head.tail) :: rem.tail.diff(List(foundComb.head)) )
      }
      iterator(list.filter(_.size > 1))
    }

    return joinListOfOperations(findMatchFor(setWE, setWB ++ setWBE))
  }

}
