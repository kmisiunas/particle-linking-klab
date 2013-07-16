package test.scala

import org.scalatest.FunSuite
import com.misiunas.kanalysis.track.ParticleTrack
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
//import main.scala.LoadFiles


/**
 * User: karolis@misiunas.com
 * Date: 13/07/2013
 * Time: 18:45
 */
class ParticleTrackTest extends FunSuite {

  def approx(d1 :Double, d2:Double, acc: Double = 0.0001) : Boolean = (Math.abs(d1-d2)<acc)


  test("ParticleTrack to JSON and back again") {
    val pt = new ParticleTrack(5)
    pt.positions = List( List(0,1.1,0.1,0),
      List(1,1.5,0.3,0),
      List(2,0.9,-0.3,0),
      List(3,2,-0.3,0),
      List(4,1.7,0.1,0),
      List(5,2.2,-0.1,0))
    val enc = pt.toJSON
    //println(enc)
    //decript
    val pt2 = ParticleTrack.constructJSON(enc)
    //println(pt2.toJSON)
    assert(pt == pt2)
  }

}

