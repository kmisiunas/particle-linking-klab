package klab.track.analysis.specialised

import com.misiunas.geoscala.Point
import klab.track.Track
import klab.track.geometry.position.Pos
import klab.track.operators.TimeOperator
import klab.track.assemblies.TrackAssembly

/**
 * == Estimates diffusion rate ==
 *
 * Diffusion rate is related to diffusion flux:
 *
 * Diffusion Flux = Diffusion Rate / Area
 *
 * Created by kmisiunas on 11/12/2013.
 */
object DiffusionRate {

  /** Method determines transition rate at specified point along specified line
    *
    * Note: uses LQPos to have consistency */
  def across(along: Point => Double, at: Double): Iterable[Track] => Map[String,Double] =
    ta => {

      def recursive(list: List[Pos], last: Pos, forward: Long, total: Long): (Long, Long) = {
        if (list.isEmpty) return (forward, total)
        (along(list.head), along(last)) match {
          case (x1,x0) if x0 <= at && x1 > at  => recursive(list.tail, list.head, forward +1, total+1 )
          case (x1,x0) if x0 >= at && x1 < at  => recursive(list.tail, list.head, forward, total+1 )
          case _ => recursive(list.tail, list.head, forward, total )
        }
      }

      val tmp = ta.map( t => recursive(t.list.tail, t.list.head, 0, 0)).unzip
      val forward = tmp._1.sum
      val total = tmp._2.sum
      val backward = total - forward

      val timeRange = TimeOperator.range(ta)

      Map("n" -> total.toDouble,
          "forward" -> forward.toDouble,
          "backward" -> backward.toDouble,
          "time interval" -> (timeRange._2 - timeRange._1),
          "rate" -> (forward - backward).toDouble / (timeRange._2 - timeRange._1)
      )
    }
}
