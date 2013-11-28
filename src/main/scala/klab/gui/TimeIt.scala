package klab.gui

import klab.gui.Print.log

/**
 * == Times an executable function ==
 *
 * Usage:
 * TimeIt( functionToExecute ) will print time it took to execute that function!
 *
 *
 * Created by kmisiunas on 28/11/2013.
 */
object TimeIt {

  def apply(execute: => Any): Any = {
    val initTime = System.currentTimeMillis()
    val res = execute
    val endTime = System.currentTimeMillis()
    val dT = endTime - initTime
    if (dT < 10000) log("TimeIt","Executed in "+ dT + " ms")
    else log("TimeIt","Executed in "+ (dT.toFloat / 1000) + " s")
    return res
  }

}
