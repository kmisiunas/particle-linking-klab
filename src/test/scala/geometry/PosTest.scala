package geometry

import klab.track.geometry.position.Pos
import klab.track.ParticleTrack
import org.scalatest.FunSuite

/**
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 15:38
 */
class PosTest extends FunSuite {

  val p1 = Pos(1.1,2.2,3.3,4.4)

  test("Pos.equals") {
    assert(p1 == Pos(1.1,2.2,3.3,4.4))
    assert(p1 != Pos(1.1,2.2,3.3,4.2))
  }

  test("Pos to JSON and back again") {
    val enc = p1.toJSON
    //println(enc)
    assert(enc == "[1.1,2.2,3.3,4.4]")
    val p2 = Pos.fromJSON(enc)
    assert(p1 == p2)
  }

}


