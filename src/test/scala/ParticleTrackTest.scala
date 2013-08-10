package test.scala

import org.scalatest.FunSuite
import com.misiunas.klab.track.ParticleTrack
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import com.misiunas.klab.track.geometry.position.Pos

//import main.scala.LoadFiles


/**
 * User: karolis@misiunas.com
 * Date: 13/07/2013
 * Time: 18:45
 */
class ParticleTrackTest extends FunSuite {

  def approx(d1 :Double, d2:Double, acc: Double = 0.0001) : Boolean = (Math.abs(d1-d2)<acc)

  val pt1 = ParticleTrack(1, List( Pos(10,1,1), Pos(11,1.3,1), Pos(12,1.2,1), Pos(13,1.1,1), Pos(14,2,2) ))
  val pt2 = ParticleTrack(2, List( Pos(10,2,3), Pos(11,1.3,1), Pos(20,1.2,1), Pos(13,1.1,1), Pos(14,2,2) )) //special
  val pt3 = ParticleTrack(2, List( Pos(10,2,3), Pos(11,1.3,1), Pos(13,1.1,1), Pos(14,2,2),   Pos(20,1.2,1) ))
  val pt4 = ParticleTrack(2, List( Pos(10,2,3), Pos(11,1.3,1), Pos(13,1.1,1), Pos(14,2,2),   Pos(20,1.2,1), Pos(24,1.2,1), Pos(25,1.2,1), Pos(28,1.2,1) ))
  val pt5 = ParticleTrack(3, List( Pos(1,2,3) ))


  test("ParticleTrack to JSON and back again") {
    val enc = pt1.toJSON
    //println(enc)
    val pt_dec = ParticleTrack.fromJSON(enc)
    //println(pt2.toJSON)
    assert(pt1 == pt_dec)
  }

  test("ParticleTrack basic functionality") {
    assert(pt1(0) == Pos(10,1,1))
    assert(pt1.size == 5)
  }

  test("ParticleTrack qualityCheck() and timeOrder()") {
    assert(pt1.qualityCheck)
    assert(!pt2.qualityCheck)
    val p = pt2.timeOrder
    assert(p == pt3, "error:"+p.toJSON) //TODO: this test sometimes fails!!!!?
  }

  test("ParticleTrack timeRange() and range()") {
    assert(pt4.timeRange == (10.0, 28.0))
    assert(pt1.range == (Pos(10, 1,1,0), Pos(14,2,2,0)))
  }

  test("ParticleTrack findAtTimeIdx() and findAtTime()") {
    assert(pt4.findAtTimeIdx(10) == 0)
    assert(pt4.findAtTimeIdx(28) == pt4.size-1)
    assert(pt4.findAtTimeIdx(23) == 4)
    assert(pt4.findAtTimeIdx(12) == 1)
    //println("pos:"+pt4.findAtTimeIdx(13.5))
    assert(pt4.findAtTime(13.501) == Pos(13,1.1,1))
  }



}

