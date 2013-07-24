package com.misiunas.klab.track.analysis

import com.misiunas.klab.track.formating.CompatibleWithCSV
import com.misiunas.klab.track.assemblies.Assembly
import com.misiunas.klab.track.position.Pos
import scalax.chart._
import scalax.chart.Charting._

/**
 * == Position Histogram ==
 *
 * An analysis tool for binning particle positions to a 1D mapping
 *
 * User: karolis@misiunas.com
 * Date: 24/07/2013
 * Time: 15:41
 */
class PosHistogram private (val occurrences: List[Int], // number of times particle appears in certain pos
                            val sliceAt: List[Double], // slice positions
                            val posMapping : (Pos => Double) // way to map Pos to a linear map
                            ) extends CompatibleWithCSV[PosHistogram]{

  /** Shows the histogram */
  def show() = {

    val chart = XYBarChart(occurrences.tail.zip(sliceAt).toXYSeriesCollection("some points"))
    chart.show
  }

  /**
   * Produces a readable CVS file.
   */
  def toCVS: String = ???
}

object PosHistogram {
  /** represents a slice of 1D histogram (Number of elements, lower pos, upper pos) */
  type Slice = (Int, Double, Double)

  /**
   * Creates a histogram out of particle assembly
   * @param pt the assembly to make a histogram from
   * @param sliceAt number of slices alon the
   * @param path the path defining the slicing direction (usually Pos.x?)
   */
  def apply(pt: Assembly, sliceAt: List[Double], path: (Pos => Double)) : PosHistogram = {
    val idxSliceAt = sliceAt.zipWithIndex
    val size = sliceAt.size
    val array : Array[Int] = Array.ofDim(sliceAt.size+1)
    def addVal(r: Double) = {
      val pos = idxSliceAt.find(r>_._1)
      if(pos.isEmpty) array(size) = array(size) + 1
      else array(pos.get._2) = array(pos.get._2) + 1
    }
    pt.foreach(_.list.foreach(p => addVal(path(p))))
    return new PosHistogram(array.toList, sliceAt, path)
  }




}