package klab.gui.infrastructure.show

import breeze.linalg._
import breeze.plot._
import klab.gui.Print

/**
 * == Method for quick showing DenseMatrix, Breeze ==
 * User: kmisiunas
 * Date: 19/11/2013
 */
object ShowDenseMatrix extends ShowType {

  /** Returns true if this handler can deal with this data type */
  def isType(list: Seq[AnyRef]): Boolean = list.head match {
    case x:DenseMatrix[Double] => true
    case _ => false
  }

  /** Returns iterator that will be written to a file - a line for each string */
  def show(list: Seq[AnyRef]): Unit = list.head match {
    case head:DenseMatrix[Double] => {
      // plot
      // 1 - get x, if it is supplied
      val size = list.size
      // estimate y
      val z: DenseMatrix[Double] = head
      val f = Figure()
      val p = f.subplot(0)
      p += image(z)
      // p += plot(x, x :^ 3.0, '.') //symbol alteration example
      //p.xlabel = "x axis"
      //p.ylabel = "y axis"
    }
    case _ => Print.error("Error: this type could not be shown after recognition")
  }

}
