package test.scala

import org.scalatest.FunSuite
import klab.track.ParticleTrack
import klab.track.geometry.position.Pos



/**
 * User: karolis@misiunas.com
 * Date: 13/07/2013
 * Time: 18:45
 */
class ParticleTrackTest extends FunSuite {

  val pt1 = ParticleTrack(1, List( Pos(10,1,1), Pos(11,1.3,1), Pos(12,1.2,1), Pos(13,1.1,1), Pos(14,2,2) ))
  val pt2 = ParticleTrack(2, List( Pos(10,2,3), Pos(11,1.3,1), Pos(20,1.2,1), Pos(13,1.1,1), Pos(14,2,2) )) //special
  val pt3 = ParticleTrack(2, List( Pos(10,2,3), Pos(11,1.3,1), Pos(13,1.1,1), Pos(14,2,2),   Pos(20,1.2,1) ))
  val pt4 = ParticleTrack(2, List( Pos(10,2,3), Pos(11,1.3,1), Pos(13,1.1,1), Pos(14,2,2),   Pos(20,1.2,1), Pos(24,1.2,1), Pos(25,1.2,1), Pos(28,1.2,1) ))
  val pt5 = ParticleTrack(3, List( Pos(1,2,3) ))


  ignore("ParticleTrack to JSON and back again") {
    val enc = pt1.toJson
    println(enc)
    val pt_dec = ParticleTrack(enc.mkString("\n"))
    //println(pt2.toJSON)
    assert(pt1 == pt_dec)
  }

  test("ParticleTrack basic functionality") {
    assert(pt1(0) == Pos(10,1,1))
    assert(pt1.size == 5)
  }

  ignore("ParticleTrack qualityCheck() and timeOrder()") {
    assert(pt1.isTimeOrdered, "pt1.isTimeOrdered was false, should be true")
    //assert(!pt2.isTimeOrdered, "pt2.isTimeOrdered was true, should be false") auto time ordering on construction is enabled!
    val p = pt2.timeOrder
    assert(p == pt3, "error:"+p.toJson) //TODO: this test sometimes fails!!!!?
  }

  test("ParticleTrack timeRange() and range()") {
    assert(pt4.timeRange == (10.0, 28.0), "ups! pt4.timeRange= "+ pt4.timeRange)
    assert(pt1.range == (Pos(10, 1,1,0), Pos(14,2,2,0)), "ups! pt1.range= "+ pt1.range)
  }

  test("ParticleTrack findAtTimeIdx() and findAtTime()") {
    assert(pt4.atTimeIdx(10) == 0)
    assert(pt4.atTimeIdx(28) == pt4.size-1)
    assert(pt4.atTimeIdx(23) == 4)
    assert(pt4.atTimeIdx(12) == 1)
    //println("pos:"+pt4.findAtTimeIdx(13.5))
    assert(pt4.atTime(13.501).get == Pos(13,1.1,1), "ups! pt4.atTime(13.501)=" + pt4.atTime(13.501).get)
  }



}

