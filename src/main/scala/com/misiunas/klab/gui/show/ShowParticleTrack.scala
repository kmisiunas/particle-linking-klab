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

class ShowParticleTrack (val listPT: Array[ParticleTrack]) extends ProcessingWindow{

  val range : track.STRange = {
    val min = Pos(
      listPT.map(_.range._1).sortBy(_.t).head.t,
      listPT.map(_.range._1).sortBy(_.x).head.x,
      listPT.map(_.range._1).sortBy(_.y).head.y)
    val max = Pos(
      listPT.map(_.range._2).sortBy(-_.t).head.t,
      listPT.map(_.range._2).sortBy(-_.x).head.x,
      listPT.map(_.range._2).sortBy(-_.y).head.y)
    (min, max)
  }

  val units = listPT.head.units

  val noOfElements = (range._2.t - range._1.t).toInt

  val physicalWidth: Float = (range._2.x - range._1.x).toFloat
  val physicalHeight: Float = (range._2.y - range._1.y).toFloat

  val physicalCenter: Point = (range._2 + range._1) * 0.5


  override def drawContent = {
    drawChannel()
    resetColorPallet()
    listPT.foreach(pt =>
    {
      drawTrack(pt);
      nextColor()
    })
  }

  // -------- Specialised drawings -------------

  private def drawChannel(): Unit = {
    stroke(204, 102, 0)
    fill(20)
    rect(-usefulWidth/2, -usefulHeight/2, usefulWidth, usefulHeight)
  }

  private def drawTrack(pt: ParticleTrack): Unit = {
    val typicalDeltaT = (range._2.t - range._1.t) / pt.size
    /** brush under the line */
    def prepBrushBG(): Unit = {
      stroke(20, 200)             // background to make it more clear
      strokeWeight(1.5f)
      strokeCap(PConstants.SQUARE)
    }
    /** brush for the line */
    def prepBrushLine(opacity: Double): Unit = {
      stroke(getColor,(opacity*255).toInt)
      strokeWeight(1.0f)
      strokeCap(PConstants.ROUND)
    }
    /** Draws single path between tho points */
    def drawPath(p1:Pos, p2:Pos, opacity: Double) = {
      if(!p1.quality && !p2.quality) { // unexpected time separation
        prepBrushBG()
        wiggle(rX(p1.x), rY(p1.y), rX(p2.x), rY(p2.y)) // draw main line
        prepBrushLine(opacity)
        wiggle(rX(p1.x), rY(p1.y), rX(p2.x), rY(p2.y)) // draw main line
      } else { // normal time separation
        prepBrushBG()
        line(rX(p1.x), rY(p1.y), rX(p2.x), rY(p2.y)) // draw main line
        prepBrushLine(opacity)
        line(rX(p1.x), rY(p1.y), rX(p2.x), rY(p2.y)) // draw main line
      }
    }

    def iterateDraw(list: List[Pos], baseOpacity : Double) = {
      val dO = (1-baseOpacity)/list.size // for fading effect
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

    if (showOnlyFrame < 0) iterateDraw(pt.list, 1)
    else {
      val idxEnd = pt.findAtTimeIdx(showOnlyFrame)+1
      val part = pt.list.slice(idxEnd- 60, idxEnd)
      if (part.size > 2){
        legend.add(getColor, "id="+pt.id)
        iterateDraw(part, 0.2)
      }
    }
  }

  // -------- outer communication --------------

  var showOnlyFrame: Double = -1

  /** draws all the frames in the particle track */
  def drawAllFrames = { showOnlyFrame = -1 }

  /** Draws frames indicated with a leading frame */
  def drawFrames(frame: Double) = {
    showOnlyFrame = frame
  }

  override def toString: String =
    if (listPT.size == 1) listPT.head.toString
    else listPT.size + " Particle Tracks"

}


object ShowParticleTrack {

  // TODO: separate thread!
  def show(pt: ParticleTrack) = (new ShowParticleTrackFrame).show(new ShowParticleTrack(Array(pt)))

  def show(list: Iterable[ParticleTrack]) = (new ShowParticleTrackFrame).show(new ShowParticleTrack(list.toArray))

}
