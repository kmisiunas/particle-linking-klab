package com.misiunas.klab.track.geometry.position

import com.misiunas.klab.track.formating.CompatibleWithJSON
import net.liftweb.json._
import com.misiunas.klab.track.geometry.{Point, GeoFeature}

/**
 *  == Representation of single physical position in time and space ==
 *
 *  It is stored as a list with
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 14:19
 */
class Pos protected (val t: Double, override val x:Double, override val y:Double, override val z:Double )
  extends Point (x,y,z)
  with CompatibleWithJSON[Pos] {

  //TODO: make only single copy of each object in the system? Is this possible? and is it efficient?

  // ------------------  Access Methods ---------------

  /** quick access tot he elements */
  def apply(i: Int) : Double = i match {
    case 0 => t
    case 1 => x
    case 2 => y
    case 3 => z
  }
  /** returns a tuple of the position */
  def get = (t,x,y,z)

  def list = List(t,x,y,z)

  def toPoint: Point = Point(x,y,z)

  // ------------------  Manipulation Methods ---------------

  //TODO: implement latter when we know what we want

  /** Modifies the value the position for specified component */
  def set(comp: Any, value: Double) : Pos = comp match {
    case 0 | "t" | "time" | 't' => Pos(value,x,y,z)
    case 1 | "x" | "pos_x" | 'x' => Pos(t,value,y,z)
    case 2 | "y" | "pos_y" | 'y' => Pos(t,x,value,z)
    case 3 | "z" | "pos_z" | 'z' => Pos(t,x,y,value)
    case _ => throw new Exception("Error: the Pos.set component could not be interpreted. You passed: "+comp)
  }

  /** get the same position with Low Quality mark */
  def toLQPos: LQPos = LQPos(t,x,y,z)

  // ------------------  Other Methods ---------------

  override def equals(other: Any): Boolean  = other match {
    case that: Pos => (that.t == t && that.x == x && that.y == y && that.z == z)
    case _ => false
  }

  override def toString = "Pos("+t+", "+x+", "+y+", "+z+")"

  def mkString = "("+x+", "+y+", "+z+") at t="+t

  def toJSON : String = compact(render(JsonDSL.seq2jvalue(list.map(JsonDSL.double2jvalue(_)))))

  def fromJSON(st:String) : Pos = Pos.fromJSON(st)

  /** Get time difference = this - that*/
  def dT(that: Pos) = t - that.t

  /** is this a quality Pos? */
  def quality: Boolean = true
}

object Pos {
  // creating the Position vectors!
  def apply(t:Double, x:Double, y:Double = 0, z:Double = 0) : Pos = new Pos(t,x,y,z)
  def apply(x: (Double, Double, Double, Double)) : Pos = Pos(x._1,x._2,x._3,x._4)
  def apply(x: (Double, Double, Double)) : Pos = Pos(x._1,x._2,x._3)
  def apply(l: List[Double]) : Pos = if (l.size == 4) new Pos(l(0), l(1), l(2),l(3)) else
    if (l.size == 3) Pos(l(0), l(1), l(2),0) else
    throw new Exception("Error: Pos could not be created from a list:"+l)
  def apply(ar: Array[Double]) : Pos = if (ar.size == 4) Pos(ar(0),ar(1),ar(2),ar(3)) else
  if (ar.size == 3) Pos(ar(0),ar(1),ar(2),0) else
    throw new Exception("Error: Pos could not be created from a array:"+ar)
  def apply(json: String) : Pos = Pos.fromJSON(json)

  def fromJSON(st:String) : Pos = {
    implicit val formats = net.liftweb.json.DefaultFormats
    Pos(parse(st).extract[List[Double]])
  }
}

