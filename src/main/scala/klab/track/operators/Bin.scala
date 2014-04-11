package klab.track.operators

import breeze.linalg.DenseVector

/**
 * == 1D binning methods ==
 *
 * Created by kmisiunas on 08/12/2013.
 */
object Bin {

  /** Method for binning values
    *
    * @param bins - supplied bins, must be always growing with index
    * @param values - list of values to bin: (Bin at, Add this value)
    * @return vector containing binned values
    */
  def apply(bins: DenseVector[Double], values: List[(Double, Double)]): DenseVector[Double] = {
    val to = DenseVector.zeros[Double]( bins.length )

    /** binary search of ordered list - O(log(n))*/
    def findIndex(x: Double, min: Int = 0, max: Int = bins.length-1): Option[Int] = {
      def recursive(min: Int, max: Int): Option[Int] = min + ((max - min) / 2) match {
        case _ if max < min =>
          if ((bins(max) - x).abs < (bins(min) - x).abs) Some(max) else Some(min)
        case mid if bins(mid) > x => recursive(min, mid-1)
        case mid if bins(mid) < x => recursive(mid+1, max)
        case mid => Some(mid)
      }
      if (x<bins(0) || x>bins(bins.length-1))  None
      else recursive(0, bins.length-1)
    }


    def iterate(left: List[(Double, Double)]): Unit = {
      if(left.isEmpty) return ()
      findIndex( left.head._1 ) match {
        case Some(id) => to(id) += left.head._2
        case None => ()
      }
      iterate(left.tail)
    }

    iterate(values)
    return to
  }


}
