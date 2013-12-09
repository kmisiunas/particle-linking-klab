package klab.track.geometry.position

import com.misiunas.geoscala.Point
import klab.io.formating.ExportJSON
import play.api.libs.json.{Json, JsValue}

/**
 *  == Representation of single physical position in time and space ==
 *
 *  Immutable object
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 14:19
 */
class Pos protected (val t: Double, override val x:Double, override val y:Double, override val z:Double )
  extends Point (x,y,z)
  with ExportJSON {

  override protected def makeFrom(e1: Double, e2: Double, e3: Double): Pos = new Pos(t, e1,e2,e3) // experimntal!

  // ------------------  Access Methods ---------------

  /** quick access tot he elements */
  override def apply(i: Int) : Double = i match {
    case 0 => t
    case 1 => x
    case 2 => y
    case 3 => z
  }
  /** returns a tuple of the position */
  def get = (t,x,y,z)

  def list = List(t,x,y,z)

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

  override def toString = if (z == 0) "Pos("+t+", "+x+", "+y+")" else "Pos("+t+", "+x+", "+y+", "+z+")"

  def mkString = "("+x+", "+y+", "+z+") at t="+t

  def toJsonValue: JsValue = if (z == 0) Json.arr(t, x, y) else Json.arr(t, x, y, z)

  /** Get time difference = this - that*/
  def dT(that: Pos) = t - that.t

  /** is this a quality Pos? */
  def quality: Boolean = true
  def isAccurate: Boolean = quality

  /** Adds all elements including time */
  def ++ (p: Pos): Pos = Pos(p.t + t, p.x + x, p.y + y, p.z + z)
  /** Multiples all the elements in Pos vector */
  def ** (d: Double): Pos = Pos(t*d, x*d, y*d, z*d)

  /** returns maximum value between two points */
  def max(that: Pos): Pos = Pos( this.list.zip(that.list).map(v => Math.max(v._1, v._2)) )

  /** returns minimum value between two points */
  def min(that: Pos): Pos = Pos( this.list.zip(that.list).map(v => Math.min(v._1, v._2)) )

}

object Pos {
  // creating the Position vectors!
  def apply(t:Double, x:Double, y:Double = 0, z:Double = 0) : Pos = new Pos(t,x,y,z)
  def apply(t:Double, p:Point) : Pos = new Pos(t,p.x,p.y,p.z)
  def apply(x: (Double, Double, Double, Double)) : Pos = Pos(x._1,x._2,x._3,x._4)
  def apply(x: (Double, Double, Double)) : Pos = Pos(x._1,x._2,x._3)

  def apply(l: List[Double]): Pos = l.size match {
    case 1 => throw new Exception("Not enough parameters supplied to make Pos: "+l)
    case 2 => Pos.apply(l(0), l(1))
    case 3 => Pos.apply(l(0), l(1), l(2))
    case 4 => Pos.apply(l(0), l(1), l(2), l(3))
    case 5 if l(4) == -1.0 => LQPos.apply(l(0), l(1), l(2), l(3))
    case _ => throw new Exception("Could not create Pos from: "+l)
  }

  def apply(ar: Array[Double]): Pos = apply(ar.toList)

  def apply(json: String): Pos = ???

}

