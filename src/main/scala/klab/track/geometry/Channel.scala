package klab.track.geometry

import com.misiunas.geoscala.volumes.{Volume, BoxXY}
import com.misiunas.geoscala.Point
import klab.io.formating.{ImportJSON, ExportJSON}
import play.api.libs.json.{Json, JsValue}
import klab.KLab
import com.misiunas.geoscala.vectors.Vec
import klab.track.geometry.infrastructure.RectangleChannel

/**
 * == Semi 1D Channel  ==
 *
 * Specification:
 *  - Expect to have Length and Width
 *  - Might be at an angle to XY axes
 *  - Contain 3 regions: inlet, channel, outlet
 *  - Description of the channel (optional)
 *
 * Constraints:
 *  - inlet and inside volumes must share an edge
 *  - outlet and inside must share an edge
 *  - 
 *
 * Features:
 *  - Determine channel angle to X by considering distribution of particles
 *  - Can be saves and loaded in JSON
 *  - Mark tracks that leave channel through the walls as LQPos
 *  -
 *
 * Version: 0.1.7
 * User: karolis@misiunas.com
 * Date: 25/07/2013
 */
abstract class Channel(val name: String, // the name of the channel (id)
                       val direction: Vec,
                       val middle: Point,
                       val size: Vec, // assuming aligned along x. y - width and z - height
                       val inletLength: Double,
                       val outletLength: Double
  ) extends Volume with ExportJSON {

  // ===  Properties of the channel == 
  val inlet: Volume
  val outlet: Volume
  val volume: Volume
  val innerVolume: Volume

  def width: Double = size.y
  def length: Double = size.x

  val along: Point => Double = direction.toEq

  // === Volume properties ===

  def isWithin(p: Point): Boolean = volume.isWithin(p)
  def distance(p: Point): Double = volume.distance(p)

  // === Other Methods ===

  override def toString: String = "Channel("+name+")"

  def toJsonValue: JsValue = {
    Json.obj( "Channel" -> Json.obj(
      "name" -> name,
      "direction" -> direction.toList,
      "middle" -> middle.toList,
      "size" -> size.toList,
      "inletLength" -> inletLength,
      "outletLength" -> outletLength,
      "for_reference" -> Json.obj(
        "KLab version" -> KLab.appVersion
    )))
  }

}

object Channel extends ImportJSON[Channel] {

  def apply(name: String, direction: Vec, middle: Point, size: Vec,
            inletLength: Double, outletLength: Double): Channel =
    RectangleChannel(name, direction, middle, size, inletLength, outletLength)

  /** simple, wide channel along X axis */
  def alongX(name: String, startAt: Double, length: Double, entranceRatio: Double = 0.1): Channel =
    Channel(name,
      direction = Vec.x,
      middle = Point(startAt + length/2, 0 , 0),
      size = Vec(length, length*2, length*2),
      inletLength = length*entranceRatio,
      outletLength = length*entranceRatio)

  def fromJson(json: String): Channel = {
    val j = Json.parse(json)
    apply(
      name = (j \ "Channel" \ "name").as[String],
      direction = Vec( (j \ "Channel" \ "direction").as[List[Double]] ),
      middle = Vec( (j \ "Channel" \ "middle").as[List[Double]] ),
      size = Vec( (j \ "Channel" \ "size").as[List[Double]] ),
      inletLength = (j \ "Channel" \ "inletLength").as[Double],
      outletLength = (j \ "Channel" \ "outletLength").as[Double]
    )
  }
}