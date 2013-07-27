package com.misiunas.klab.track.analysis

import com.misiunas.klab.track.formating.CompatibleWithJSON
import org.joda.time.DateTime
import com.misiunas.klab.track.assemblies.Assembly
import com.misiunas.klab.track.geometry.Channel
import com.misiunas.klab.track.ParticleTrack

/**
 * == Class for analysing transition across the channel ==
 *
 * Show compute:
 *  - transition type (forward, backward, forward return, backward return)
 *  - time spent in the channel

 * User: karolis@misiunas.com
 * Date: 25/07/2013
 * Time: 21:35
 */
class Transition private (val list : List[Transition.TrackAnalysis],
                                  val time: Long = System.currentTimeMillis())
  extends CompatibleWithJSON[Transition]{

  lazy val countForwardReturns = list.filter(_._2 == 2).size
  lazy val countForwardTransitions = list.filter(_._2 == 1).size
  lazy val countUnfinishedTracks = list.filter(_._2 == 0).size
  lazy val countBackwardTransitions = list.filter(_._2 == -1).size
  lazy val countBackwardReturns = list.filter(_._2 == -2).size

  lazy val meanTimeForwardReturns = list.filter(_._2 == 2).map(_._3).sum / countForwardReturns
  lazy val meanTimeForwardTransitions  = list.filter(_._2 == 1).map(_._3).sum / countForwardTransitions
  lazy val meanTimeUnfinishedTracks = list.filter(_._2 == 0).map(_._3).sum / countUnfinishedTracks
  lazy val meanTimeBackwardTransitions = list.filter(_._2 == -1).map(_._3).sum / countBackwardTransitions
  lazy val meanTimeBackwardReturns = list.filter(_._2 == -2).map(_._3).sum / countBackwardReturns

  def numberOfEntries = list.size

  private def formatStatement(name: String, tr: Int, dt: => Double) =
    name + ": count="+tr + (if(tr>0) (", mean time="+dt+"\n") else "\n")


  override def toString: String = "Channel Transition analysis (on "+DateTime.now.toString("H:m Y-M-d")+")\n" +
    formatStatement("Forward Returns", countForwardReturns, meanTimeForwardReturns) +
    formatStatement("Forward Transitions", countForwardTransitions, meanTimeForwardTransitions) +
    formatStatement("Incomplete tracks", countUnfinishedTracks, meanTimeUnfinishedTracks) +
    formatStatement("Backward Transitions", countBackwardTransitions, meanTimeBackwardTransitions) +
    formatStatement("Backward Returns", countBackwardReturns, meanTimeBackwardReturns)

  def toJSON: String = ???
  def fromJSON(st: String): Transition = ???
}

object Transition {

  /**
   * Defines the result's of the analysis:
   *  - id : Int
   *  - type : Int = -2 for backward return
   *                 -1 for backward transition
   *                 0 for broken path inside the channel
   *                 1 for forward transition
   *                 2 for forward return
   *  - time in channel : Double
   */
  type TrackAnalysis = (Int, Int, Double)

  def apply(ta: Assembly, ch: Channel) : Transition = {

    def analyseTrack(pa: ParticleTrack) : TrackAnalysis  = {
      //TODO: Generalise
      val inCh = pa.list.filter(ch.isWithin(_))
      val b = inCh.head
      val e = inCh.last
      val bB = ch.isWithinBeginning(b)
      val bE = ch.isWithinEnding(b)
      val eB = ch.isWithinBeginning(e)
      val eE = ch.isWithinEnding(e)
      val kind : Int = if (bB && eE) 1 else
        if (bE && eB) -1 else
        if (bB && eB) 2 else
        if (bE && eE) -2 else 0
      return (pa.id, kind, inCh.last.t - inCh.head.t)
    }

    def iterate(ta: List[ParticleTrack]) : List[TrackAnalysis] =
      if (ta.isEmpty) return Nil
      else analyseTrack(ta.head) :: iterate(ta.tail)

    new Transition( iterate(ta.toList) )
  }
}
