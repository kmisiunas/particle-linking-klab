package com.misiunas.klab.gui.show

import processing.core.PApplet
import processing.core._
import com.misiunas.klab.track.ParticleTrack
import com.alee.laf.slider.WebSlider
import javax.swing.{JFrame, SwingConstants}
import scala.swing.{Orientation, BoxPanel, MainFrame, Frame}
import com.misiunas.klab.track
import com.misiunas.klab.track.geometry.position.Pos
import com.misiunas.klab.track.geometry.Point

/**
 * User: karolis@misiunas.com
 * Date: 21/07/2013
 * Time: 21:12
 */

class ShowParticleTrack (var pt :ParticleTrack) extends ProcessingWindow{

  var range : track.STRange = pt.range

  val physicalWidth: Float = (range._2.x - range._1.x).toFloat
  val physicalHeight: Float = (range._2.y - range._1.y).toFloat

  val physicalCenter: Point = (range._2 + range._1) * 0.5


  override def drawContent = {
    drawChannel
    drawTrack
  }

  // -------- Specialised drawings -------------

  private def drawChannel = {
    stroke(204, 102, 0)
    fill(20)
    rect(-usefulWidth/2, -usefulHeight/2, usefulWidth, usefulHeight)
  }

  private def drawTrack = {
    val typicalDeltaT = (range._2.t - range._1.t) / pt.size

    def drawPath(p1:Pos, p2:Pos, opacity: Double) = {
      stroke(20)             // background to make it more clear
      strokeWeight(1.5f)
      strokeCap(PConstants.SQUARE)
      line(rX(p1.x), rY(p1.y), rX(p2.x), rY(p2.y))
      if(p2.t - p1.t > typicalDeltaT*1.1) stroke(color(230,30,30,(opacity*255).toInt))   // color variation depending on time separation - 10% diffrences
      else if(p2.t - p1.t < typicalDeltaT*0.9) stroke(color(30,230,30,(opacity*255).toInt))
      else stroke(230,(opacity*255).toInt)
      strokeWeight(1.0f)
      strokeCap(PConstants.ROUND)
      line(rX(p1.x), rY(p1.y), rX(p2.x), rY(p2.y)) // draw main line
    }

    def iterateDraw(list: List[Pos], baseOpacity : Double) = {
      val dO = (1-baseOpacity)/list.size
      var opacity = baseOpacity
      def iterate(list: List[Pos], prev: Pos) : Unit = {
        if(!list.isEmpty) {
          opacity = opacity + dO
          drawPath(prev, list.head, opacity)
          iterate(list.tail, list.head)
        }
      }
      iterate(list.tail, list.head)
    }

    if(showPosInATrack < 0) iterateDraw(pt.list, 1)
    else iterateDraw(pt.list.take(showPosInATrack+1).takeRight(60), 0.2)
  }

  // -------- outer communication --------------

  var showPosInATrack = -1

  /** draws all the frames in the particle track */
  def drawAllFrames = { showPosInATrack = -1 }

  /** Draws frames indicated with a leading frame */
  def drawFrames(id: Int) = {
    if(id<2) {
      showPosInATrack = 2
    }
    else showPosInATrack = id
  }

}


object ShowParticleTrack {

  // TODO: separate thread!
  def show(pt: ParticleTrack) = (new ShowParticleTrackFrame).show(new ShowParticleTrack(pt))

}
