package klab.io.infrastructure.save

import breeze.linalg.{DenseVector, DenseMatrix}
import scala.annotation.tailrec

/**
 * ## Saves arrays in form of vector or matrix in CSV
 *
 * User: kmisiunas
 * Date: 13/11/2013
 */
object SaveArrayAnyVal extends SaveType {

  override val defaultFileExtension = ".csv"
  override val kind: String = "csv"

  /** Returns true if this handler can deal with this data type */
  def isType(that: Any): Boolean = that match {
    case x: Array[AnyVal] => true
    case _ => false
  }

  /** Returns iterator that will be written to a file - a line for each string */
  def getWriter(that: Any, path: String): Iterator[String] = that match {
    case x: Array[AnyVal] => x.map(_.toString).toIterator // fix: instead of fromArrayToIterator
    case _ => throw new UnsupportedOperationException("SaveArrayAnyVal can't handle this type")
  }


  /** Creates iterator from a 1D array. Interprets neighbours as rows */
  def fromArrayToIterator[B<:AnyVal](data: Array[B], columns: Int, rows: Int): Iterator[String] = {
    def loopTheRows(row: Int, acc: Iterator[String]): Iterator[String] = {
      if (row >= rows) return acc
      return loopTheRows(row + 1, acc ++ Iterator(data.slice(row*columns, (row+1)*(columns)).mkString("", ",", ",")))
    }
    loopTheRows(0, Iterator())
  }

  /** Creates iterator from a 1D array. Interprets neighbours as columns */
  def fromArrayToIteratorC[B<:AnyVal](data: Array[B], columns: Int, rows: Int): Iterator[String] = {
    @tailrec
    def loopTheRows(row: Int, acc: Iterator[String]): Iterator[String] = {
      if (row >= rows) return acc
      else {
        val rowIdx = Range(row, rows*columns, rows)
        return loopTheRows(row + 1, acc ++ Iterator( ( for (i <- rowIdx) yield data(i) ).mkString("", ",", ",")) )
      }
    }
    loopTheRows(0, Iterator())
  }

}
