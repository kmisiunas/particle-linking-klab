package klab.track.builders

import klab.track.geometry.position.Pos
import klab.track.ParticleTrack


/**
 * == Constructs Particle Tracks from a list of Pos ==
 *
 * Use track.m as a basis for this tracking routine
 *
 * Code Aims:
 *  - modular system with focus on clarity
 *  - expandability - will want to use same code for when we have features in our particles.
 *  - be prepared for tracking errors - missing particles or ones that exist without particle
 *
 * Guidelines:
 *  - Once done, id tracks in order of first appearance.
 *  - maximumJump is a parameter that measures maximum displacement to consider it as the same track
 *  - Do not bother connecting broken tracks - this will be done by other code
 *  - Do not bother looking for overlaps - it will be done by other code
 *
 * Method:
 *  - Make a distinction between two cases: trivial and complex
 *    - Trivial cases should be the ones where there is only one particle following another
 *    - Trivial cases should be connected straight to Segments
 *  - Segments are collections of Pos that have been lined together they will become tracks.
 *    - Complex cases should be resolved by listing all possible connections and selecting the one with lowest costing function
 *  - CostingFunction should be estimated by diffusive likelihood - distance
 *
 *
 * Created by kmisiunas on 03/01/2014.
 */
object TracksFromPos {

  def track(maximumJump: Double): List[Pos] => List[ParticleTrack] =
  raw => {

    // Slow implementation - have one pool for all the times

    val range: (Time, Time) = (raw.minBy(_.t).t.toInt, raw.maxBy(_.t).t.toInt)

    val timeMap: Map[Time, Set[Pos]] = (range._1 to range._2).map( t => (t, raw.filter(_.t == t).toSet) ).toMap









    ???
  }

  // ============ Types for clarity ===========

  protected type Time = Int

  /** stores part of the path */
  protected type Segment =  List[Pos]
  protected object Segment{
    def apply(s: Pos*): Segment = s.toList
  }


  // =========== Key Functions =========

  protected def connect(atT: Time, all: Map[Time, Set[Pos]], unfinished: Set[Segment], finished: Set[Segment] ): Set[Segment] = {
    ???
  }




  def costingFunction = ???


  // =========== Helper functions ========

  protected def quickFind(atTime: Double): List[Segment] => List[Segment] = ???


}
