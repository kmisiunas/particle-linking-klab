package klab.track.geometry.position

import play.api.libs.json.{Json, JsValue}

/**
 * == low quality position ==
 *
 * It is used as identifier for poor quality points that should not be used for some computations.
 *
 * User: karolis@misiunas.com
 * Date: 08/08/2013
 * Time: 22:35
 */
class LQPos private
  (override val t: Double, override val x:Double, override val y:Double, override val z:Double )
  extends Pos (t,x,y,z)  {

  override def toString = "LQPos("+t+", "+x+", "+y+", "+z+")"

  /** the 5th element is set "-1.0" and indicates that it is LQPos */
  override def toJsonValue: JsValue =  Json.arr(t, x, y, z, -1.0)

  def toPos: Pos = Pos(t,x,y,z)

  override def quality: Boolean = false

}

object LQPos {

  def apply(t:Double, x:Double, y:Double=0, z:Double=0) : LQPos = new LQPos(t,x,y,z)
}