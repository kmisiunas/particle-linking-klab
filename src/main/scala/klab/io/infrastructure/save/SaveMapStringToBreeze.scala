package klab.io.infrastructure.save

import breeze.linalg.{DenseVector, DenseMatrix}
import java.lang.UnsupportedOperationException

/**
 * ## Save a map containing String -> Breeze vectors
 *
 * Specification:
 *  - Save in CSV file format
 *  - First row for titles of the column
 *  - alphabetical order
 *  - Use In built OpenCVS engine:
 *   - http://www.scalanlp.org/api/index.html#breeze.io.CSVWriter$
 *   - http://opencsv.sourceforge.net/#what-is-opencsv
 *
 * Version: 0.1.5
 * User: kmisiunas
 * Date: 09/01/2014
 */
object SaveMapStringToBreeze extends SaveType {

  override val defaultFileExtension = ".csv"
  override val kind: String = "csv"

  /** Returns true if this handler can deal with this data type */
  def isType(that: Any): Boolean = that match {
    case x: Map[String, DenseVector[Double]] => true
    case x: Map[String, DenseVector[Int]] => true
    case _ => false
  }

  /** Returns iterator that will be written to a file - a line for each string */
  def getWriter(that: Any, path: String): Iterator[String] = that match {
    case x: Map[String, DenseVector[Double]] => formatIterator(x)
    case x: Map[String, DenseVector[Int]] => ???
    case _ => throw new UnsupportedOperationException("This version of SaveType can't handle this type")
  }

  /** function for doing this */
  def formatIterator(map: Map[String, DenseVector[Double]]): Iterator[String] = {
    val list = map.toList.sortBy(_._1)
    val header = list.map(_._1).mkString("",",",",")
    val numbers = list.map( _._2 )
    def takeIfExists(i: Int)(v: DenseVector[Double]): Double = if (i>=0 && i<v.length) v(i) else Double.NaN
    def takeRow(i: Int): String = numbers.map( takeIfExists(i)(_) ).mkString("",",",",")
    val maxNumberOfRows = numbers.map(_.length).max

    Iterator(header) ++ (for (i <- 0 until maxNumberOfRows) yield takeRow(i)).toIterator
  }
}
