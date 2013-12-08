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
      if (max < min) return None
      val mid = min + ((max - min) / 2)
      if(bins(mid) > x) findIndex(x, min, mid-1)
      else if(bins(mid) < x) findIndex(x, mid+1, max)
      else Option(mid) // found
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
