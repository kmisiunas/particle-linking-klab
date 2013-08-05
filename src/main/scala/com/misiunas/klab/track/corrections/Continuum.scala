package com.misiunas.klab.track.corrections

import com.misiunas.klab.track.assemblies.{TrackAssembly, Assembly}
import com.misiunas.klab.track._
import com.misiunas.klab.track.geometry.position.Pos
import com.misiunas.klab.track.geometry.{Everywhere, Point, GeoVolume, GeoSurface}
import scala.collection.SetLike

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

  final val filterSizeMin = 3

  /** finds elements that are not entering through specified surfaces
    * @return (set containing tracks with wrong beginnings only, with wrong beginnings and ends, with wrong ends)
    */
  def find(withinChannel: GeoVolume) : Iterable[ParticleTrack] => (Set[ParticleTrack], Set[ParticleTrack], Set[ParticleTrack]) =
  ta => {
    val wrongEnd = ta.filter(pt => withinChannel.isWithin(pt.last)).toSet // find tracks that ends are inside the volume
    // find tracks that ends begin within the volume - they should begin outside
    val wrongBeginning = ta.filter(pt => withinChannel.isWithin(pt.head)).toSet
    val wrongBandE = wrongEnd & wrongBeginning // have both wrong beginning and end
    ( (wrongBeginning &~ wrongBandE), wrongBandE, (wrongEnd &~ wrongBandE) )
  }


  /** new line declaration */
  def pairUp1D(withinChannel: GeoVolume,
               dX_ : Double = 0,  // the tolerance along the line, 0 for automatic
               dT_ : Double = 0,  // the tolerance in time search, 0 for automatic
               line : (Point => Double) = _.x,
               messages: Boolean = true) : List[ParticleTrack] => List[ParticleTrack] =
  assembly => {
    throw new Exception("Method not finished!")
    val ta = Filter.bySize(3)(assembly) // other are too small to resolve
    // correct parameters
    val dX = if(dX_ > 0) dX_ else {
        val l = ta.map(t => line((t.range._2 - t.range._1))/t.size)
        l.sum / l.size * 15*3*4
    }
    val dT = if(dT_ > 0) dT_ else {
      val l = ta.map(t => (t.timeRange._2-t.timeRange._1)/t.size)
      30 //l.sum / l.size * 15
    }
    println("dX="+dX)
    println("dT="+dT)
    // messaging override
    def println(s:String) = if(messages) System.out.println("  ;"+s)
    println("Starting Continuum.pairUp1D algorithm with "+assembly.size+" tracks")
    println(" - Applying Filter.bySize to remove small tracks. Rummaging tracks: "+ta.size)
    val (tracksWB, setWBE, tracksWE) = Continuum.find(withinChannel)(ta)
    val fragments = Filter.bySize(2,10)(setWBE).toSet  // fragments - short tracks!
    val tracksWBE = setWBE &~ fragments
    println(" - Applying Continuum.find. Found non-continuous tracks: WB.size="+tracksWB.size+", WBE.size="+tracksWBE.size+", WE.size="+tracksWE.size)
    println(" - And "+ fragments.size +" fragments were found")

    def findEnds(p: Pos): Set[ParticleTrack] = (tracksWBE ++ tracksWE).
      filter(t => {val b = t.last; (line(p) - line(b)).abs <= dX && (p.t - b.t).abs <= dT })

    def findBeginnings(p: Pos): Set[ParticleTrack] =  (tracksWB).
      filter(t => {val b = t.head; (line(p) - line(b)).abs <= dX && (p.t - b.t).abs <= dT })

    // join up tracks

    def iterate(needsEnd: Set[ParticleTrack], needsBeginning: Set[ParticleTrack]) : List[List[ParticleTrack]] = {
      if (needsEnd.isEmpty) return Nil
      val track = needsEnd.head
      val closeEnds = findEnds(track.last) &~ Set(track)
      val closeBeginnings = findBeginnings(track.last)

      // join up tracks

      (closeBeginnings.size, closeEnds.size) match {
        case (_,0) => {
          println("No End track could be found for track: " + track)
          return  iterate(
            needsEnd.tail ++ (closeEnds & tracksWBE),
            needsBeginning &~ closeEnds)
        }
        case (0,1) => { // one track case
          if (!needsBeginning(closeEnds.head)) sys.error("Ups! The track was used somewhere else already: " + track)
          return List(track,closeEnds.head) :: iterate(
            needsEnd.tail ++ (Set(closeEnds.head) & tracksWBE),
            needsBeginning &~ Set(closeEnds.head))
        }
        case (1,2) => { // two tracks case - Use CoM - code can be optimised!
          if ((needsBeginning & closeEnds) != closeEnds) sys.error("Ups! The tracks were used somewhere else already: " + track)
          val other = closeBeginnings.head
          val trackCoM = track.list.takeRight(10).map(line(_)).sum
          val otherCoM = other.list.takeRight(10).map(line(_)).sum
          val sortedEnds = closeEnds.toList.sortBy(_.list.take(10).map(line(_)).sum)
          if(trackCoM < otherCoM){ // head belongs to track!
            return List(track,closeEnds.head) :: List(other, closeEnds.tail.head) :: iterate(
              needsEnd.tail ++ (closeEnds & tracksWBE),
              needsBeginning &~ closeEnds)
          } else { // tail.head belongs to track
            return List(track,closeEnds.tail.head) :: List(other, closeEnds.head) :: iterate(
              needsEnd.tail ++ (closeEnds & tracksWBE),
              needsBeginning &~ closeEnds)
          }
        }
        case _ => sys.error("Problem: there were " + closeEnds.size + " ends and " + closeBeginnings.size + "beginnings next to the " + track)
      }
    }

    // insert fragments where possible

    val joints : List[List[ParticleTrack]] = sortJoints( iterate(tracksWE, tracksWBE ++ tracksWB) )
    println(" - finding joints: there are "+joints.map(_.size-1).sum)
    val newTracks = combineJoints(assembly.last.id+1, joints)
    println(" - and "+newTracks.size+" new tracks were constructed. New ID are in the range: ["+newTracks.last.id+","+newTracks.head.id+"]")
    // left overs? <- print
    // print /\
    ( (ta.toSet &~ joints.flatten.toSet) ++ newTracks ).toList
  }

  private def sortJoints(list: List[List[ParticleTrack]]) : List[List[ParticleTrack]] = joinListOfOperations(list)

  private def combineJoints(idStrat: Int, list: List[List[ParticleTrack]]): List[ParticleTrack] = {
    var newIDs: Int = idStrat - 1 // current top id
    def getNewID: Int = {newIDs = newIDs+1; newIDs}
    list.map(m => ParticleTrack(getNewID, m, "ParticleTrack"))
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

  /**
   * Function pairs up unfinished tracks
   * @param setWB set of tracks that are missing beginnings
   * @param setWBE set of tracks that are missing beginnings and endings
   * @param setWE set of tracks that are missing endings
   * @param dT time diffrence to look at
   * @return  a list that specifies which tracks should be combined into single ones
   */
  private def findPairs(setWB:Set[ParticleTrack],
             setWBE: Set[ParticleTrack],
             setWE: Set[ParticleTrack],
             dT: Double) :  List[List[ParticleTrack]] = {
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
      //def line(p: Point): Double = p.x
      //val thisCoM : Double =
      //  pt.list.takeRight(10).map(line(_)).sum / pt.list.takeRight(10).size
      //val fnCoM : (ParticleTrack => Double) = ptThis => (thisCoM - ptThis.list.take(10).map(line(_)).sum / ptThis.list.take(10).size).abs
      val contTrack = needsBeginning
        .filter(t => {val dt = t.head.dT(pt.last); (dt>0 && dt<dT)}) // find tracks that begin when this ends in short time interval
        .filter(t => t.head.distance(pt.last).abs <= 6) // TODO remove harcoded parameter!
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

  private def endlessCorrections(list: List[List[ParticleTrack]], setWithoutEnds: Set[ParticleTrack]): List[List[ParticleTrack]] = {
    list.filterNot(l => setWithoutEnds(l.last))
  }

  /**
   * Automatically finds discontinuous tracks and joins them up.
   * Also removes the tracks that could not be made continuous and small sub-tracks that are likely to be noise
   */
  def pairUp(withinChannel: GeoVolume,
             dT_ : Double = 0,  // the tolerance in time search, 0 for automatic
             messages: Boolean = true) : List[ParticleTrack] => List[ParticleTrack] =
  assembly => {
    def println(s:String): Unit = if(messages) System.out.println(s) // printing override
    println("Straiting Continuum.pairUp for Assembly with " + assembly.size + " tracks")
    val ta: List[ParticleTrack] = Filter.bySize(filterSizeMin)(assembly) // filter out small elements
    println(" - found "+ta.size+" tracks after applying a Filter.bySize(>="+ filterSizeMin + ")")
    val dT = if(dT_ > 0) dT_ else {
      val l = ta.map(t => (t.timeRange._2-t.timeRange._1)/(t.size-1))
      l.sum / l.size * 8
    }
    println(" - time separation filter was set to dT=" + dT.toString.take(5))
    val (tracksWB, tracksWBE, tracksWE) = Continuum.find(withinChannel)(ta)
    println(" - with Continuum.find found "+(tracksWB ++ tracksWBE ++ tracksWE).size +" non-continuous tracks:")
    println(" - of which by type: WB.size="+tracksWB.size+", WBE.size="+tracksWBE.size+", WE.size="+tracksWE.size)
    val joints = Continuum.findPairs(tracksWB, tracksWBE, tracksWE, dT)
    println(" - found "+joints.map(_.size-1).sum +" joints")
    //val usefulJoints = endlessCorrections(joints, tracksWBE ++ tracksWE)
    //println(" - of which "+usefulJoints.map(_.size-1).sum +" made complete tracks")
    val newTracks = combineJoints(assembly.size+1, joints)
    println(" - thus "+newTracks.size +" new tracks were constructed with id = [" + newTracks.head.id + ", " + newTracks.last.id+"]")
    // a word about discarded tracks
    val unusedTracks = ((tracksWB ++ tracksWBE ++ tracksWE) &~ joints.flatten.toSet)
    println(" - there were " + unusedTracks.size + " tracks that could no be matched")
    //((tracksWB ++ tracksWBE ++ tracksWE) &~ usefulJoints.flatten.toSet).toList.sortBy(_.id).foreach( p => println(p.toString) )
    val usedTracks = ((tracksWB ++ tracksWBE ++ tracksWE) & joints.flatten.toSet)
    (ta.toSet &~ usedTracks).toList ::: newTracks.reverse  // returns the new list
  }

  /** function determines the quality of a track assembly */
  def qualityCheck(ta: Assembly) : Boolean = ???


}