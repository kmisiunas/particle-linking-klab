package com.misiunas.klab.track.analysis

import com.misiunas.klab.track.geometry.position.Pos

/**
 * == Estimate the diffusion coefficient ==
 *
 * User: karolis@misiunas.com
 * Date: 26/07/2013
 * Time: 13:31
 */
object Diffusion {

  def mean1D(line: Pos => Double): Iterable[Pos] => Double =
  list => {
    // primitive method, todo: update
    def sq(x:Double): Double = x*x
    val packed = list.foldLeft( (Pos(0,0).toLQPos: Pos, 0: Double, 0: Int) )( (pack, thisPos) => {
      // pack._1 is last Pos; pack._2 is accumulator, pack._3 is nu of elements
      val (lastPos, sum, noOfElements) = pack
      if (lastPos.quality && thisPos.quality) {
        val dT = thisPos.dT(lastPos)
        val dX = sq(line(thisPos) - line(lastPos))
        (thisPos, sum + dX/(2*dT), noOfElements + 1)
      } else { // there is LQPos - skip
        (thisPos, sum, noOfElements)
      }
    })
    packed._2 / packed._3 // compute mean
  }

}
