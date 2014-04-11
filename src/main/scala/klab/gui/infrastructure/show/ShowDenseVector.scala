//package klab.gui.infrastructure.show
//
//import klab.track.Track
//import klab.gui.Print
//import breeze.linalg._
//import breeze.plot.Figure
//import breeze.plot._
//
///**
// * == Quick show Dense vector, Breeze ==
//
// * User: kmisiunas
// * Date: 19/11/2013
// */
//object ShowDenseVector extends ShowType {
//
//  /** Returns true if this handler can deal with this data type */
//  def isType(list: Seq[AnyRef]): Boolean = list.head match {
//    case x:DenseVector[Double] => true
//    case _ => false
//  }
//
//  /** Returns iterator that will be written to a file - a line for each string */
//  def show(list: Seq[AnyRef]): Unit = list.head match {
//    case head:DenseVector[Double] => {
//      // plot
//      // 1 - get x, if it is supplied
//      val size = list.size
//      val x: DenseVector[Double] =
//        if (size >= 2) list.last.asInstanceOf[DenseVector[Double]]
//        else linspace(1.0, head.length, head.length)
//      // estimate y
//      val y: List[DenseVector[Double]] =
//        if (size >= 2) list.dropRight(1).map( _.asInstanceOf[DenseVector[Double]] ).toList
//        else list.map( _.asInstanceOf[DenseVector[Double]] ).toList
//      val f = Figure()
//      val p = f.subplot(0)
//      y.foreach( p += plot(x, _) )
//      // p += plot(x, x :^ 3.0, '.') //symbol alteration example
//      //p.xlabel = "x axis"
//      //p.ylabel = "y axis"
//    }
//    case _ => Print.error("Error: this type could not be shown after recognition")
//  }
//
//}
