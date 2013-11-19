package io

import org.scalatest.FunSuite
import klab.io.Path
import breeze.linalg.DenseMatrix
import klab.io.infrastructure.save.SaveBreeze

/**
 *
 * User: kmisiunas
 * Date: 13/11/2013
 */
class SaveBreezeTest extends FunSuite{

  val mInt: DenseMatrix[Int] = DenseMatrix.zeros(2,3)

  val m: DenseMatrix[Double] = DenseMatrix.zeros(2,3)
  m.update(0,0,1)
  m.update(0,2,2)
  m.update(1,0,3)

  val ans = List("1.0,0.0,2.0," , "3.0,0.0,0.0,")

  test("SaveBreeze.getWriter") {
    val gen = SaveBreeze.getWriter(m,"").toList
    assert(gen == ans)
  }

  test("SaveBreeze.isType [Integer]") {
    assert(SaveBreeze.isType(mInt))
  }

}
