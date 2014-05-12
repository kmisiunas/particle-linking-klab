package geometry

import klab.track.geometry.position.Pos
import klab.track.Track
import org.scalatest.FunSuite

/**
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 15:38
 */
class PosTest extends FunSuite {

  val p1 = Pos(1.1,2.2,3.3,4.4)
  val p2 = Pos(2,-1,4,0)

  test("Pos.equals") {
    assert(p1 == Pos(1.1,2.2,3.3,4.4))
    assert(p1 != Pos(1.1,2.2,3.3,4.2))
  }

  ignore("Pos to JSON and back again") {
    val enc = p1.toJson
    //println(enc)
    assert(enc == "[1.1,2.2,3.3,4.4]")
    //val p2 = Pos.fromJSON(enc)
    assert(p1 == p2)
  }

  test("Pos.min and Pos.max") {
    assert(p1.max(p2) == Pos(2, 2.2, 4, 4.4))
    assert(p1.min(p2) == Pos(1.1, -1, 3.3, 0))
  }

}


