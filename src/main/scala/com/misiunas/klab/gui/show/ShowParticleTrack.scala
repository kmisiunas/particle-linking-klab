package com.misiunas.klab.gui.show

import processing.core.PApplet
import processing.core._
import com.misiunas.klab.track.ParticleTrack
import com.alee.laf.slider.WebSlider
import javax.swing.{JFrame, SwingConstants}
import scala.swing.{Orientation, BoxPanel, MainFrame, Frame}
import com.misiunas.klab.track
import com.misiunas.klab.track.position.Pos

/**
 * User: karolis@misiunas.com
 * Date: 21/07/2013
 * Time: 21:12
 */

class ShowParticleTrack (var pt :ParticleTrack) extends PApplet{

  // PROCESSING.org!

  val widthWindow = 600
  val heightWindow = 500

  var range : track.STRange = pt.range

  /** the width of the channel to be displayed */
  val channelWidth = if((range._2.x-range._1.x)/widthWindow > (range._2.y-range._1.y)/heightWindow) widthWindow else
    (widthWindow * (range._2.x-range._1.x) / (range._2.y-range._1.y)).toFloat

  /** the height of the channel to be displayed */
  val channelHeight = if((range._2.x-range._1.x)/widthWindow < (range._2.y-range._1.y)/heightWindow) heightWindow else
    (heightWindow * (range._2.y-range._1.y) / (range._2.x-range._1.x)).toFloat

  override def setup = {
    size(widthWindow,heightWindow)
    background(200)
    //noLoop() // draw once and then upon asking
    frameRate(4)
    smooth()
  }

  override def draw = {
    background(200)
    translate( (width-channelWidth)/2,
      (height-channelHeight)/2) // channel at the center
    stroke(204, 102, 0)
    fill(20)
    rect(0, 0, channelWidth, channelHeight)

    val typicalDeltaT = (range._2.t - range._1.t) / pt.size

    def drawPath(p1:Pos, p2:Pos) = {
      stroke(20, 100)             // background to make it more clear
      strokeWeight(1.5f)
      line(rX(p1.x), rY(p1.y), rX(p2.x), rY(p2.y))
      if(p2.t - p1.t > typicalDeltaT*1.1) stroke(230,30,30)   // color variation depending on time separation - 10% diffrences
      else if(p2.t - p1.t < typicalDeltaT*0.9) stroke(30,230,30)
      else stroke(230)
      strokeWeight(1.0f)
      line(rX(p1.x), rY(p1.y), rX(p2.x), rY(p2.y)) // draw main line
    }

    def iterateDraw(list: List[Pos], prev: Pos) : Boolean = {
      if(list.isEmpty) return true
      drawPath(prev,list.head)
      iterateDraw(list.tail, list.head)
    }
    iterateDraw(pt.list.tail, pt.list.head)

  }

  /** scale coordinates to match the screen size */
  def rX( x: Double) : Float = (channelWidth * (x -range._1.x) / (range._2.x-range._1.x)).toFloat
  def rY( y: Double) : Float = (channelHeight * (y -range._1.y) / (range._2.y-range._1.y)).toFloat

  /** Method for updating an image */
  def update1 = {
    draw()
  }

}


object ShowParticleTrack {

  // TODO: separate thread!

  def show(pt: ParticleTrack) = {
    val tmp = new ShowParticleTrack(pt)
    println("point1: "+tmp)
    (new ShowParticleTrackFrame).show( tmp )
  }

}
