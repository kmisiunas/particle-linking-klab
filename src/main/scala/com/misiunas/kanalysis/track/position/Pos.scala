package com.misiunas.kanalysis.track.position

import com.misiunas.kanalysis.track.formating.CompatibleWithJSON
import net.liftweb.json._

/**
 *  == Representation of single physical position in time and space ==
 *
 *  It is stored as a list with
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 14:19
 */
class Pos private (val list: List[Double]) extends CompatibleWithJSON[Pos] {

  //TODO: make only single copy of each object in the system? Is this possible? and is it efficient?

  // ------------------  Access Methods ---------------

  /** get time */
  def t = list(0)
  /** get x coordinate*/
  def x = list(1)
  /** get y coordinate */
  def y = list(2)
  /** get z coordinate*/
  def z = list(3)
  /** quick access tot he elements */
  def apply(i: Int) : Double = list(i)
  /** returns a tuple of the position */
  def get = (t,x,y,z)

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



  // ------------------  Other Methods ---------------

  override def equals(other: Any): Boolean  = other match {
    case that: Pos => that.list == list
    case _ => false
  }

  override def toString = "Pos("+t+", "+x+", "+y+", "+z+")"

  def mkString = "("+x+", "+y+", "+z+") at t="+t

  def toJSON : String = compact(render(JsonDSL.seq2jvalue(list.map(JsonDSL.double2jvalue(_)))))

  def fromJSON(st:String) : Pos = Pos.fromJSON(st)

}

object Pos {
  // creating the Position vectors!
  def apply(t:Double, x:Double, y:Double, z:Double) : Pos = new Pos(List(t,x,y,z))
  def apply(t:Double, x:Double, y:Double) : Pos = Pos(t,x,y,0)
  def apply(t:Double, x:Double) : Pos = Pos(t,x,0,0)
  def apply(x: (Double, Double, Double, Double)) : Pos = Pos(x._1,x._2,x._3,x._4)
  def apply(x: (Double, Double, Double)) : Pos = Pos(x._1,x._2,x._3)
  def apply(list: List[Double]) : Pos = if (list.size == 4) new Pos(list) else
    if (list.size == 3) Pos(list(0),list(1),list(2),0) else
    throw new Exception("Error: Pos could not be created from a list:"+list)
  def apply(ar: Array[Double]) : Pos = if (ar.size == 4) Pos(ar(0),ar(1),ar(2),ar(3)) else
  if (ar.size == 3) Pos(ar(0),ar(1),ar(2),0) else
    throw new Exception("Error: Pos could not be created from a array:"+ar)
  def apply(json: String) : Pos = Pos.fromJSON(json)

  def fromJSON(st:String) : Pos = {
    implicit val formats = net.liftweb.json.DefaultFormats
    Pos(parse(st).extract[List[Double]])
  }
}

