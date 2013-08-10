package track.analysis

import org.scalatest.FunSuite
import com.misiunas.klab.track.ParticleTrack
import com.misiunas.klab.track.geometry.position.Pos
import com.misiunas.klab.track.assemblies.TrackAssembly
import com.misiunas.klab.track.geometry.Channel
import com.misiunas.klab.track.analysis.Proximity

/**
 * User: karolis@misiunas.com
 * Date: 06/08/2013
 * Time: 00:30
 */
class ProximityTest extends FunSuite {

  val pt1 = ParticleTrack(1, List( Pos(10,0,1), Pos(11,1,1), Pos(12,2,1), Pos(13,3,1), Pos(14,4,2) ))
  val pt2 = ParticleTrack(2, List( Pos(16,6,3), Pos(17,5,1), Pos(18,6,1), Pos(19,7,1), Pos(20,8,2), Pos(21,10,2) ))
  val pt3 = ParticleTrack(3, List( Pos(20,5,3), Pos(21,4,1), Pos(23,3,1), Pos(24,2,2),   Pos(25,1.2,1) ))
  val pt4 = ParticleTrack(4, List( Pos(35,10,3), Pos(36,9,1), Pos(37,8,1), Pos(38,7,2),   Pos(39,6,1), Pos(40,5,1), Pos(41,4,1), Pos(42,3,1),Pos(43,2,1),Pos(44,1,1),Pos(45,3,1),Pos(46,1.2,1) ))
  val pt5 = ParticleTrack(5, List( Pos(50,4,1), Pos(51,3,1), Pos(52,2,1), Pos(53,3,1), Pos(54,4,2) ))

  val ta = TrackAssembly( List(pt1,pt2,pt3,pt4,pt5), "Test Experiment")

  val channel = Channel.simpleAlongX(2,8,6)

  test("Proximity.find") {
    assert(Proximity.find(pt2)(ta).toSet == Set(pt3))
  }

}
