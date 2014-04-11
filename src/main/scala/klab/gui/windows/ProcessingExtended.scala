package klab.gui.windows

import processing.core.{PConstants, PApplet}
import scala.collection.mutable

/**
 * User: karolis@misiunas.com
 * Date: 08/08/2013
 * Time: 20:35
 */
abstract class ProcessingExtended extends PApplet {

  val fontMelno = loadFont("Menlo.vlw") // monospaced font (32) located in resources/data/


  /** legend of the window */
  val legend = new Legend(mutable.Buffer())

  class Legend( val list: mutable.Buffer[(Int, String)]) {
    def add(color: Int, name: String): Unit = list.append((color, name))
    def reset(): Unit = list.clear()
    def draw(x: Float, y:Float): Unit = {
      if (list.isEmpty) return ()
      strokeWeight(1)
      stroke(255)
      fill(10,150)
      rect(x, y, 110, list.size*14+6, 7)
      textFont(fontMelno)
      textSize(12)
      textAlign(PConstants.LEFT, PConstants.TOP);
      var h = 3
      list.foreach(v => {
        fill(v._1)
        stroke(v._1)
        line(x+5, y+h+7,x+5+30, y+h+7)
        text(v._2 , x+5+35, h)
        h = h+14
      })
    }
  }



  // ---------- additional drawing methods ------------

  /** draw wiggly line */
  def wiggle(x0: Float, y0: Float, x1: Float, y1: Float, times: Int = 0): Unit = {
    val length = PApplet.sqrt((x0-x1)*(x0-x1) + (y0-y1)*(y0-y1))
    val n: Int = if (times <= 0) (length /8).toInt else times // default curl
    val r: Float = length/n
    ellipseMode(PConstants.CENTER)
    noFill()
    val angle: Float = Math.atan2(y1-y0,x1-x0).toFloat
    var i = 0
    while (i<n) {
      val x: Float = x0 + (x1-x0)*(i+0.5f)/n
      val y: Float = y0 + (y1-y0)*(i+0.5f)/n
      arc(x, y, r, r, angle + PConstants.PI * i, angle + PConstants.PI * (i+1))
      i = i+1
    }
  }

}
