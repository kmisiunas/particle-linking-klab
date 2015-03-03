package klab.gui.windows

import processing.core.{PVector, PConstants, PApplet}
import org.joda.time.DateTime
import com.misiunas.geoscala.Point

/**
 * == A general processing window for displaying tracks or assemblies of tracks ==
 *
 * Functionality:
 *  - zoom in/out buttons on left bottom coroner
 *  - click and drag to move view around
 *  - right to measure distance (TODO)
 *  - take a picture call from other process
 *  - update image call from other process
 *  - hovering displays coordinate
 *
 * User: karolis@misiunas.com
 * Date: 28/07/2013
 * Time: 01:05
 */
abstract class ProcessingWindow extends ProcessingExtended {

  // ----------- TO IMPLEMENT in sub-class ----------

  /** the width of the channel to be displayed */
  val physicalWidth: Float

  /** the height of the channel to be displayed */
  val physicalHeight: Float

  /** the point at which the physical map begins */
  val physicalCenter: Point

  // ------------- Auto Color generator -------------

  private var currentColorPicker = 0

  /** reset color picker */
  def resetColorPallet(): Unit = {currentColorPicker = 0}

  /** set to next color in the pallet */
  def nextColor(): Unit =
    if (currentColorPicker < defaultColorList.size-1) currentColorPicker = currentColorPicker+1
    else currentColorPicker = 0

  /** get current color */
  def getColor: Int = defaultColorList( currentColorPicker )

  lazy val defaultColorList = Array(
    color(255),
    color(255,0,0),
    color(0,255,0),
    color(0,0,255),
    color(255,0,255),
    color(0,255,255),
    color(255,255,0)
  )

  // ------------- Variables and constants ----------

  val colorBG = color(237)  // background color

  /** at what stage is the zoom */
  var zoomLevel : Float = 1

  /** useful width an heights in which to scale the drawing */
  var usefulWidth : Int = 0
  var usefulHeight : Int = 0

  // ------- Main Methods ---------

  override def setup() = {
    size(600,400)
    //frame.setResizable(true)
    initiation
    noLoop() // draw once and then upon asking
    frameRate(12)
    smooth()
  }

  /** initiation function - should be called after window resize */
  def initiation() = {
    usefulWidth  = if(physicalWidth/width > physicalHeight/height) width else
                     (height * physicalWidth/physicalHeight).toInt
    usefulHeight = if(physicalWidth/width < physicalHeight/height) height else
                     (width * physicalHeight/physicalWidth).toInt
  }

  override def draw() = {
    background(colorBG)
    legend.reset()
    pushMatrix()
    translate(width/2, height/2) //set the drawing in the right position
    scale(zoomLevel) // zoom
    translate(viewCoord.x,viewCoord.y) // current translation by the user
    drawContent

    popMatrix()  // reset for the rest
    //drawCursor
    legend.draw(0,0)
  }

  // ----------- Coordination --------------------

  /** translates experiment coordinates into window coordinates */
  def rX( x: Double) : Float = ((x - physicalCenter.x) * usefulWidth / physicalWidth).toFloat
  def rY( y: Double) : Float = ((y - physicalCenter.y) * usefulHeight / physicalHeight).toFloat

  /** translates window coordinates into simulation coordinates */
  def rXT(rx: Float) : Double = ((rx - width/2)/ zoomLevel - viewCoord.x) * physicalWidth / usefulWidth + physicalCenter.x
  def rYT(ry: Float) : Double = ((ry - height/2)/ zoomLevel - viewCoord.y) * physicalHeight / usefulHeight + physicalCenter.y

  // --------- Drawing methods ------------------------

  //var coordProbe = new PVector(-1,-1)

  def drawCursor = {
    if(mouseX > 2 && mouseX < width-3 && mouseY > 2 && mouseY < height-3 ) { // only draw if the mouse is within the frame
      stroke(color(255, 80, 40, 250))
      strokeWeight(1)
      fill(color(255, 255, 0, 170))
      textFont(fontMelno)
      textSize(12)
      val coordinates = rXT(mouseX).toString.take(5) +"; "+ rYT(mouseY).toString.take(5)
      rect(mouseX, mouseY, -(textWidth(coordinates)+6), -14, 4, 4, 0, 4) // rounded box
      fill(color(0, 0, 0))
      textAlign(PConstants.RIGHT, PConstants.BOTTOM);
      text(coordinates , mouseX,mouseY)
    }
  }

  /** abstract class - should be implemented by adding content drawing to it */
  def drawContent : Unit

  // --------- Interaction methods -----------

  override def mouseClicked = {
    drawCursor
    redraw
  }

  override def mousePressed() : Unit = {getMouseChange; loop }

  var prevMousePos : PVector = new PVector(0,0)
  def getMouseChange : PVector = {
    val n = prevMousePos.get
    n.sub(mouseX, mouseY,0)
    prevMousePos.set(mouseX,mouseY)
    return n
  }

  var viewCoord = new PVector(0,0)
  // drag mouse feature
  override def mouseDragged: Unit = {
    cursor(PConstants.HAND)
    val change = getMouseChange
    change.div(zoomLevel)
    viewCoord.sub(change)
    //println("chanege "+viewCoord)
  }

  override def mouseReleased = {cursor(PConstants.ARROW) ; noLoop }


  // --------- Communication with Outside -------------

  /** Method for updating image size*/
  def updateSize(x:Int,y:Int) = {
    size(x,y)
    initiation
    draw
  }

  def saveSnapshot = {
    val file: String = "Window Snap-Shot on "+ DateTime.now.toString("HH:mm:ss Y-MM-dd") +".png"
    save(file)
  }

  def zoom(ratio : Float) = {
    zoomLevel = zoomLevel * ratio
    if(zoomLevel > 10)  zoomLevel = 10
    else if(zoomLevel < 1) zoomLevel = 1
    redraw
  }

  /** resets the view to original position */
  def resetView = {
    zoomLevel = 1
    viewCoord = new PVector(0,0)
    redraw
  }
}