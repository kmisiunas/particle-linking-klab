import com.misiunas.kanalysis.track.position.Pos
import com.misiunas.kanalysis.track.{ParticleTrack}
import org.scalatest.FunSuite

/**
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 15:38
 */
class PosTest extends FunSuite {

  def approx(d1 :Double, d2:Double, acc: Double = 0.0001) : Boolean = (Math.abs(d1-d2)<acc)

  test("Pos to JSON and back again") {
    val p1 = Pos(1.1,2.2,3.3,4.4)
    val enc = p1.toJSON
    assert(enc == "[1.1,2.2,3.3,4.4]")
    val p2 = Pos.fromJSON(enc)
    assert(p1 == p2)
  }

}


