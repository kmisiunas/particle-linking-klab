package klab.track.corrections.specialised

import klab.track.assemblies.{TrackAssembly, Assembly}
import klab.track._
import klab.track.geometry.position.Pos
import klab.track.geometry._
import scala.collection.SetLike
import scala.annotation.tailrec
import klab.track.analysis.Diffusion
import com.misiunas.geoscala.volumes.Volume
import com.misiunas.geoscala.Point
import klab.track.corrections._

/**
 * == Enforces continuum of tracks ==
 *
 * Features:
 *  - Continuum.pairUp - links tracks that are not continuous withing channel
 *
 * Plan:
 *  1. Find non-continuous tracks within specified volume
 *  2. Classify the noncontinuity with Segment class
 *  3. Link the trivial cases, that satisfy:
 *  3.a. ones that only have one another track in immediate proximity
 *  3.b. ones with only one corresponding Segment in proximity
 *  3.c. ones with set maximum spacial and temporal distances
 *  4. Link Complex tracks: todo
 *  5. Report on linking success and form new assembly
 *
 * User: karolis@misiunas.com
 * Date: 20/07/2013
 * Time: 23:40
 */

object Continuum {

  /** for storing missing link information */
  abstract class Segment(val track: Track, val needsBeginning: Boolean, val needsEnd: Boolean){
    lazy val beginning = track.head
    lazy val end =  track.last
    override def equals(a: Any): Boolean = a match {
      case x: Segment => this.track == x.track
      case _ => false
    }
  }
  case class SegmentWB(override val track: Track) extends Segment(track, true, false)
  case class SegmentWE(override val track: Track) extends Segment(track, false, true)
  case class SegmentWBE(override val track: Track) extends Segment(track, true, true)


  /** finds elements that are not entering through specified surfaces
    * @return (set containing tracks with wrong beginnings only, with wrong beginnings and ends, with wrong ends)
    */
  def find(within: Volume): Iterable[Track] => (Set[Track], Set[Track], Set[Track]) =
  ta => {
    val wrongEnd = ta.filter(pt => within.isWithin(pt.last)).toSet // find tracks that ends are inside the volume
    // find tracks that ends begin within the volume - they should begin outside
    val wrongBeginning = ta.filter(pt => within.isWithin(pt.head)).toSet
    val wrongBandE = wrongEnd & wrongBeginning // have both wrong beginning and end
    ( (wrongBeginning &~ wrongBandE), wrongBandE, (wrongEnd &~ wrongBandE) )
  }


  /** Finds non-continuous tracks within the volume, and returns time ordered list */
  def findSegments(within: Volume) : Iterable[Track] => List[Segment] =
  ta => {
    val list = find(within)(ta)
    ( list._1.map( pt => SegmentWB(pt) ).toList :::
      list._2.map( pt => SegmentWBE(pt) ).toList :::
      list._3.map( pt => SegmentWE(pt) ).toList
    ).sortBy( _.beginning.t )
  }


  def pairUp[A <: Iterable[Track]] (channel: Channel,
                                            maxDT : Double = 10,  // the tolerance in time search, 0 for automatic
                                            messages: Boolean = true): A => A =
  assembly => {

    def println(s:String): Unit = if(messages) System.out.println(s) // printing override
    println("Straiting Continuum.pairUp for Assembly with " + assembly.size + " tracks")
    val segments = Continuum.findSegments(channel.volume)(assembly) // all segments
    val mustJoin = Continuum.findSegments(channel.innerVolume)( segments.map(_.track) ) // these must be paired
    println(" - found "+ mustJoin.size +" non-continuous tracks within the channel")

    ???

//    println(" - of which by type: WB.size="+tracksWB.size+", WBE.size="+tracksWBE.size+", WE.size="+tracksWE.size)
//    val joints = Continuum.findPairs(tracksWB, tracksWBE, tracksWE, dT,
//      experimentalMode = true, allowedOverlapT = -1)
//    println(" - found "+joints.map(_.size-1).sum +" joints")
//    val newTracks = combineJoints(assembly.size+1, joints)
//    println(" - thus "+newTracks.size +" new tracks were constructed with id = [" + newTracks.head.id + ", " + newTracks.last.id+"]")
//    val unusedTracks = ((tracksWB ++ tracksWBE ++ tracksWE) &~ joints.flatten.toSet)
//    println(" - there were " + unusedTracks.size + " tracks that could no be matched")
//    //((tracksWB ++ tracksWBE ++ tracksWE) &~ usefulJoints.flatten.toSet).toList.sortBy(_.id).foreach( p => println(p.toString) )
//    val usedTracks = ((tracksWB ++ tracksWBE ++ tracksWE) & joints.flatten.toSet)
//    returnSameType(assembly)(
//      Continuum.qualityCheck((ta.toSet &~ usedTracks).toList ::: newTracks.reverse)  // returns the new list
//    )
  }


  /* store match results here */
  case class LinkResult(newTrack: Track, from: List[Track])

  def matchTrivialSegments(context: Set[Track],
                           maxDT: Double,
                           maxX: Double)
                          (must: List[Segment], may: Set[Segment]): List[LinkResult] = {
    ???
  }


  // ---------- Helper Functions --------------


  /** Links two particle tracks inserting LQPos in between them */
  def linkTracks(t1: Track, t2: Track): Track = {
    val from = t1.last
    val to = t2.head
    val linker = if (to.t - from.t == 1.0) Nil else chainLinkLQPos(from, to)
    t1.copy(id = -1, list = t2.list ::: linker ::: t1.list)
  }

  /** linker chain creator for every frame */
  private def chainLinkLQPos(from: Pos, to: Pos): List[Pos] = {
    val frames = (to.t - from.t).toInt
    val step = (to - from) * (1.0 / frames)
    (1 to (frames-1)).map( i => Pos(from.t + i ,from + (step * i)) ).reverse.toList
  }



  /** combines joints into new tracks */
  def combineJoints(idStart: Int, list: List[List[Track]]): List[Track] = {
    var newIDs: Int = idStart - 1 // current top id
    def getNewID: Int = {newIDs = newIDs+1; newIDs}
    list.map(m => Track(getNewID, m))
  }


  /** Takes Joints and orders them and combines them into single sequence of joints */
  def sortJoints(list: List[List[Track]]): List[List[Track]] = {
    @tailrec
    def iterator(rem : List[List[Track]], acc: List[List[Track]]) : List[List[Track]] = {
      if(rem.isEmpty) return acc.reverse
      val foundComb = rem.tail.filter(rem.head.last == _.head)
      if(foundComb.isEmpty) iterator(rem.tail, rem.head :: acc)
      else iterator( (rem.head ::: foundComb.head.tail) :: rem.tail.diff(List(foundComb.head)), acc)
    }
    iterator(list.filter(_.size > 1), Nil)
  }


  // ----------- Old Methods -----------

  /** Don't use tracks that are shorter than this value for pairing up algorithm */
  @deprecated
  final val filterSizeMin = 2


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
  @deprecated
  private def findPairs(setWB:Set[Track],
                        setWBE: Set[Track],
                        setWE: Set[Track],
                        dT: Double,
                        experimentalMode: Boolean = false,
                        allowedOverlapT: Double = 0,
                        line: Point => Double = _.x) :  List[List[Track]] = {
    /** initial matching starting with the the tracks that missing the endings  */
    @tailrec
    def findMatchFor(needsEnd: List[Track],
                     needsBeginning: Set[Track],
                     acc: List[List[Track]]) : List[List[Track]] = {
      if (needsEnd.isEmpty) return acc.reverse // todo: reverse might be unnecessary here
      if (needsBeginning.isEmpty)
        throw new Exception("Warning: Set contains elements that still need beginning, but there is no ends to match them with")
      val pt : Track = needsEnd.head
      /** this function determines weighting by which the matches are made */
      val fnSeparation : (Track => Double) =
        if (experimentalMode) {
          val D: Double = 1  // todo: replace! //Diffusion.mean1D(line)(pt.list.takeRight(60)) // from pt
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


  /**
   * Automatically finds discontinuous tracks and joins them up.
   * Also removes the tracks that could not be made continuous and small sub-tracks that are likely to be noise
   */
  @deprecated
  def pairUpOld[A <: Iterable[Track]]
  (withinChannel_ : Volume,
   dT_ : Double = 0,  // the tolerance in time search, 0 for automatic
   messages: Boolean = true): A => A =
    assembly => {
      def println(s:String): Unit = if(messages) System.out.println(s) // printing override
      println("Straiting Continuum.pairUp for Assembly with " + assembly.size + " tracks")
      val ta = Filter.bySize(filterSizeMin)(assembly) // filter out small elements
      println(" - found "+ta.size+" tracks after applying a Filter.bySize(>="+ filterSizeMin + ")")

      val withinChannel = withinChannel_ match {
        case ch: Channel => ch.innerVolume
        case v: Volume => v
      }
      val dT = dT_ match {
        case dT: Double if dT > 0 => dT
        case _ => {
          val l = ta.map(t => (t.timeRange._2-t.timeRange._1)/(t.size-1))
          l.sum / l.size * 21
        }
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
      returnSameType(assembly)(
        Continuum.qualityCheck((ta.toSet &~ usedTracks).toList ::: newTracks.reverse)  // returns the new list
      )
    }


  /** Mark ParticleTracks that contain problems and mark the problem in the comment.
    * Also change Pos to LQPos for tracks that have atypical separation or time separation.
    * This might be a bad place for this. */
  @deprecated
  def qualityCheck: PTFilter =
    ta => ta.map(_.qualityCheck).toList


  /** Function adds artificial beginnings or endings for given the channel.
    * The channel will be marked as LQPos to prevent them from being used in some computations.
    * Does not chane tracks id.
    */
  def addArtificialEnd(channel: Channel): Track => Track =
    pt => {
      //    def findPlaceOutsideChannel(p: Pos): Point =
      //      if (channel.along(p) > channel.along(channel.middle))
      //        Point(channel.geometry.max.x +0.01) // only works along x, todo extend for general channel
      //      else
      //        Point(channel.geometry.min.x -0.01)
      //
      //    var list: List[Pos] = pt.list
      //    if (channel.isWithin(pt.head)) { // add at the beginning
      //      val pre = Pos(pt.head.t - 2, findPlaceOutsideChannel(pt.head)).toLQPos
      //      list = pre :: list
      //    } else if (channel.isWithin(pt.last)) { // add at the end
      //      val post = Pos(pt.last.t + 2, findPlaceOutsideChannel(pt.last)).toLQPos
      //      list = list :+ post
      //    } else {
      //      println("Continuum.addArtificialEnd : no end was missing.")
      //    }
      //    pt.appendComment("Added artificial ends").changePositions(list)
      ??? // todo: fix for new channel implementation
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

}