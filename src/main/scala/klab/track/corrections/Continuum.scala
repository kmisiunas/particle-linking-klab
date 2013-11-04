package klab.track.corrections

import klab.track.assemblies.{TrackAssembly, Assembly}
import klab.track._
import klab.track.geometry.position.Pos
import klab.track.geometry._
import scala.collection.SetLike
import scala.annotation.tailrec
import klab.track.analysis.Diffusion
import com.misiunas.geoscala.volumes.Volume
import com.misiunas.geoscala.Point

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

  /** Don't use tracks that are shorter than this value for pairing up algorithm */
  final val filterSizeMin = 3

  /** finds elements that are not entering through specified surfaces
    * @return (set containing tracks with wrong beginnings only, with wrong beginnings and ends, with wrong ends)
    */
  def find(withinChannel: Volume) : Iterable[ParticleTrack] => (Set[ParticleTrack], Set[ParticleTrack], Set[ParticleTrack]) =
  ta => {
    val wrongEnd = ta.filter(pt => withinChannel.isWithin(pt.last)).toSet // find tracks that ends are inside the volume
    // find tracks that ends begin within the volume - they should begin outside
    val wrongBeginning = ta.filter(pt => withinChannel.isWithin(pt.head)).toSet
    val wrongBandE = wrongEnd & wrongBeginning // have both wrong beginning and end
    ( (wrongBeginning &~ wrongBandE), wrongBandE, (wrongEnd &~ wrongBandE) )
  }


  /**
   * Automatically finds discontinuous tracks and joins them up.
   * Also removes the tracks that could not be made continuous and small sub-tracks that are likely to be noise
   */
  def pairUp(withinChannel: Volume,
             dT_ : Double = 0,  // the tolerance in time search, 0 for automatic
             messages: Boolean = true) : PTFilter =
  assembly => {
    def println(s:String): Unit = if(messages) System.out.println(s) // printing override
    println("Straiting Continuum.pairUp for Assembly with " + assembly.size + " tracks")
    val ta: List[ParticleTrack] = Filter.bySize(filterSizeMin)(assembly) // filter out small elements
    println(" - found "+ta.size+" tracks after applying a Filter.bySize(>="+ filterSizeMin + ")")
    val dT = if(dT_ > 0) dT_ else {
      val l = ta.map(t => (t.timeRange._2-t.timeRange._1)/(t.size-1))
      l.sum / l.size * 21
    }
    println(" - time separation filter was set to dT=" + dT.toString.take(5))
    val (tracksWB, tracksWBE, tracksWE) = Continuum.find(withinChannel)(ta)
    println(" - with Continuum.find found "+(tracksWB ++ tracksWBE ++ tracksWE).size +" non-continuous tracks:")
    println(" - of which by type: WB.size="+tracksWB.size+", WBE.size="+tracksWBE.size+", WE.size="+tracksWE.size)
    val joints = Continuum.findPairs(tracksWB, tracksWBE, tracksWE, dT,
      experimentalMode = true, allowedOverlapT = -1)
    println(" - found "+joints.map(_.size-1).sum +" joints")
    val newTracks = combineJoints(assembly.size+1, joints)
    println(" - thus "+newTracks.size +" new tracks were constructed with id = [" + newTracks.head.id + ", " + newTracks.last.id+"]")
    val unusedTracks = ((tracksWB ++ tracksWBE ++ tracksWE) &~ joints.flatten.toSet)
    println(" - there were " + unusedTracks.size + " tracks that could no be matched")
    //((tracksWB ++ tracksWBE ++ tracksWE) &~ usefulJoints.flatten.toSet).toList.sortBy(_.id).foreach( p => println(p.toString) )
    val usedTracks = ((tracksWB ++ tracksWBE ++ tracksWE) & joints.flatten.toSet)
    Continuum.qualityCheck((ta.toSet &~ usedTracks).toList ::: newTracks.reverse)  // returns the new list
  }


  /** Mark ParticleTracks that contain problems and mark the problem in the comment.
    * Also change Pos to LQPos for tracks that have atypical separation or time separation.
    * This might be a bad place for this. */
  def qualityCheck: PTFilter =
    ta => ta.map(_.qualityCheck).toList


  /** Function adds artificial beginnings or endings for given the channel.
    * The channel will be marked as LQPos to prevent them from being used in some computations.
    * Does not chane tracks id.
    */
  def addArtificialEnd(channel: Channel): ParticleTrack => ParticleTrack =
  pt => {
    def findPlaceOutsideChannel(p: Pos): Point =
      if (channel.line(p) > channel.line(channel.middle))
        Point(channel.geometry.max.x +0.01) // only works along x, todo extend for general channel
      else
        Point(channel.geometry.min.x -0.01)

    var list: List[Pos] = pt.list
    if (channel.isWithin(pt.head)) { // add at the beginning
      val pre = Pos(pt.head.t - 2, findPlaceOutsideChannel(pt.head)).toLQPos
      list = pre :: list
    } else if (channel.isWithin(pt.last)) { // add at the end
      val post = Pos(pt.last.t + 2, findPlaceOutsideChannel(pt.last)).toLQPos
      list = list :+ post
    } else {
      println("Continuum.addArtificialEnd : no end was missing.")
    }
    pt.appendComment("Added artificial ends").changePositions(list)
  }

  /** Adds artificial ends to all the tracks that are missing endings and comply to size requirements*/
  def fixEnds(channel: Channel,
             minSize: Int = 40,  // don't care for small tracks
             messages: Boolean = true) : PTFilter =
  assembly => {
    def println(s:String): Unit = if(messages) System.out.println(s) // printing override
    println("Applying Continuum.fixEnd for tracks longer than " + minSize)
    val missing = Continuum.find(channel)( Filter.bySize(minSize)(assembly) )
    val missingSet = (missing._1 ++ missing._2 ++ missing._3)
    println(" - found " + missingSet + " tracks that needed artificial ends")
    val corrected = missingSet.map( Continuum.addArtificialEnd(channel) )
    qualityCheck(assembly.toSet -- missingSet ++ corrected)
  }


  // ---------- Helper Functions --------------


  /** combines joints into new tracks */
  def combineJoints(idStart: Int, list: List[List[ParticleTrack]]): List[ParticleTrack] = {
    var newIDs: Int = idStart - 1 // current top id
    def getNewID: Int = {newIDs = newIDs+1; newIDs}
    list.map(m => ParticleTrack(getNewID, m))
  }


  /** Takes Joints and orders them and combines them into single sequence of joints */
  def sortJoints(list: List[List[ParticleTrack]]): List[List[ParticleTrack]] = {
    @tailrec
    def iterator(rem : List[List[ParticleTrack]], acc: List[List[ParticleTrack]]) : List[List[ParticleTrack]] = {
      if(rem.isEmpty) return acc.reverse
      val foundComb = rem.tail.filter(rem.head.last == _.head)
      if(foundComb.isEmpty) iterator(rem.tail, rem.head :: acc)
      else iterator( (rem.head ::: foundComb.head.tail) :: rem.tail.diff(List(foundComb.head)), acc)
    }
    iterator(list.filter(_.size > 1), Nil)
  }


  /**
   * Function pairs up unfinished tracks.
   *
   * Algorithm workings:
   *  - prefer pairing up long tracks first, they are more valuable
   *  - while pairing up start by taking endless tracks and finding an appropriate continuation
   *  - pair up tracks with that have minimal distance between end and beginnings within time interval dT
   *
   * Experimental features:
   *  - Include time in pair up algorithm: dx -> dx + 2*sqrt(D*dt)
   *  - small sequence do determine local D (approximately)
   *  - use 1D line equation to simplify the problem
   *  - restrict maximum separation to prevent strange solutions
   *
   * @param setWB set of tracks that are missing beginnings
   * @param setWBE set of tracks that are missing beginnings and endings
   * @param setWE set of tracks that are missing endings
   * @param dT time interval to search forward to for appropriate endings
   * @param experimentalMode enables most advanced features of the algorithm
   * @param allowedOverlapT (experimental) allows time overlap between end and beginning (should be small)
   * @param line (experimental) a line equation for 1D problems
   * @return  a list that specifies which tracks should be combined into single ones
   */
  private def findPairs(setWB:Set[ParticleTrack],
                        setWBE: Set[ParticleTrack],
                        setWE: Set[ParticleTrack],
                        dT: Double,
                        experimentalMode: Boolean = false,
                        allowedOverlapT: Double = 0,
                        line: Point => Double = _.x) :  List[List[ParticleTrack]] = {
    /** initial matching starting with the the tracks that missing the endings  */
    @tailrec
    def findMatchFor(needsEnd: List[ParticleTrack],
                     needsBeginning: Set[ParticleTrack],
                     acc: List[List[ParticleTrack]]) : List[List[ParticleTrack]] = {
      if(needsEnd.isEmpty) return acc.reverse // todo: reverse might be unnecessary here
      if(needsBeginning.isEmpty)
        throw new Exception("Warning: Set contains elements that still need beginning, but there is no ends to match them with")
      val pt : ParticleTrack = needsEnd.head
      /** this function determines weighting by which the matches are made */
      val fnSeparation : (ParticleTrack => Double) =
        if (experimentalMode) {
          val D: Double = Diffusion.mean1D(line)(pt.list.takeRight(60)) // from pt
          val spaceOverTime = 2 // the ratio between time and space displacements must be monitored
          ptThis => (line(ptThis.head) - line(pt.last)).abs + 2* Math.sqrt( D * ptThis.head.dT(pt.last).abs) /spaceOverTime
        } else {
          ptThis => pt.last.distance(ptThis.head)
        }
      val contTrack = needsBeginning
        .filter(t => {val dt = t.head.dT(pt.last); (dt>allowedOverlapT && dt<dT)}) // find tracks that begin when this ends in short time interval
        .filter(t => !experimentalMode || ((line(t.head) - line(pt.last)).abs <= 25)) // TODO remove hardcoded parameter!
        .toList.sortBy[Double](fnSeparation)  // sort by distance from the end
        .take(1) // take the track with shortest distance
      return findMatchFor(
        needsEnd.tail ++ (contTrack.toSet & setWBE),
        needsBeginning &~ contTrack.toSet,
        (pt :: contTrack) :: acc)
    }
    return sortJoints(
      findMatchFor(
        setWE.toList.sortBy(-_.size), // recover long tracks first - they are more valuable!
        setWB ++ setWBE, Nil))
  }


}