package klab.io

import breeze.linalg.DenseMatrix

/**
 * Simplified protocol for sending information to Wolfram Mathematica
 *
 * Created by kmisiunas on 18/03/2014.
 */
object Mathematica {

  /** gives temporary directory */
  def tmpSaveDirectory(): Path =
    if( util.Properties.isWin ) throw new Exception("Mathematica operations not supported on this OS")
    else if( util.Properties.isMac ) Path("~/Library/KLab/tmp/Methematica/")
    else throw new Exception("Mathematica operations not supported on this OS")


  /** sends */
  def apply(a: AnyRef): Unit = send(a)

  def send(a: AnyRef): Unit = {
    Save.apply(a, tmpSaveDirectory() + "export.csv")
  }

  def get(): DenseMatrix[Double] = ???

}
