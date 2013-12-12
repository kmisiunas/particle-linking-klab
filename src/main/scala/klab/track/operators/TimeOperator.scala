package klab.track.operators

import klab.track.geometry.position.Pos
import klab.track.ParticleTrack

/**
 * == Provided functions for acting on time components ==
 * 
 * Created by kmisiunas on 11/12/2013.
 */
object TimeOperator {
  
  
  /** Returns true if every frame exists in a given sequence */
  def isContinuous: Seq[Pos] => Boolean =
    l => l.tail.foldLeft( (l.head, true) )( (sum, p) => (p, sum._2 && (p.t - sum._1.t == 1.0) ) )._2

  /** finds time range for given collection of ParticleTracks */
  def range(ta: Iterable[ParticleTrack]): (Double, Double) = {
    val r = ta.map(_.timeRange).unzip
    (r._1.min, r._2.max)
  }

}
