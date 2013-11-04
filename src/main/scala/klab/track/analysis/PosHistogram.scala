package klab.track.analysis

import klab.track.assemblies.Assembly
import klab.track.geometry.position.Pos
import scalax.chart._
import scalax.chart.Charting._
import klab.track.geometry.Channel
import klab.track.formating.ExportCSV

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
                            val posMapping : (Pos => Double), // way to map Pos to a linear map
                            val experiment: String // name of the experiment
                           ) extends ExportCSV{

  /** normalisation factor for the particles within the range */
  lazy val normalisation = occurrences.tail.dropRight(1).sum

  /** Shows the histogram */
  def show() = {
    val chart = XYBarChart(sliceAt.zip(occurrences.tail.map(_*1)).toXYSeriesCollection(experiment))
    chart.show(title="Occurrence frequency at certain position")
  }

  /**
   * Produces a readable CVS file.
   */
  def toCSV: String = {
    val minRange = -Double.MaxValue :: sliceAt
    val maxRange = sliceAt :+ Double.MaxValue
    occurrences.zip( minRange.zip( maxRange ) )
      .map(el => el._2._1 + csvSeparator + el._2._2 + csvSeparator + el._1 + csvSeparator )
      .mkString("minRange, maRange, count,\n", "\n","")
  }
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
      val pos = idxSliceAt.find(r<_._1)
      if(pos.isEmpty) array(size) = array(size) + 1
      else array(pos.get._2) = array(pos.get._2) + 1
    }
    pt.foreach(_.list.foreach(p => addVal(path(p))))
    return new PosHistogram(array.toList, sliceAt, path, pt.experiment)
  }

  def apply(pt: Assembly, ch: Channel) : PosHistogram =
    PosHistogram(pt, ch.gridX, ch.line)


}