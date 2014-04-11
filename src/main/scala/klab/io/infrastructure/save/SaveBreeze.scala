package klab.io.infrastructure.save

import breeze.linalg.{DenseVector, DenseMatrix}
import java.lang.UnsupportedOperationException

/**
 * ## Save Breeze matrices and vectors
 *
 * Specification:
 *  - Save in CSV file format
 *  - Use In built OpenCVS engine:
 *   - http://www.scalanlp.org/api/index.html#breeze.io.CSVWriter$
 *   - http://opencsv.sourceforge.net/#what-is-opencsv
 *
 * Version: 0.1.5
 * User: kmisiunas
 * Date: 12/11/2013
 */
object SaveBreeze extends SaveType {

  override val defaultFileExtension = ".csv"
  override val kind: String = "csv"

  /** Returns true if this handler can deal with this data type */
  def isType(that: Any): Boolean = that match {
    case x: DenseMatrix[Double] => true
    case x: DenseMatrix[Int] => true
    case x: DenseVector[Double] => true
    case x: DenseVector[Int] => true
    case _ => false
  }

  /** Returns iterator that will be written to a file - a line for each string */
  def getWriter(that: Any, path: String): Iterator[String] = that match {
    case x: DenseMatrix[Double] => SaveArrayAnyVal.fromArrayToIteratorC(x.data, x.cols, x.rows)
    case x: DenseMatrix[Int] => SaveArrayAnyVal.fromArrayToIteratorC(x.data, x.cols, x.rows) // does not work?
    case x: DenseVector[Double] => SaveArrayAnyVal.fromArrayToIterator(x.data, 1, x.length)
    case x: DenseVector[Int] => SaveArrayAnyVal.fromArrayToIterator(x.data, 1, x.length)
    case _ => throw new UnsupportedOperationException("This version of SaveType can't handle this type")
  }

}
