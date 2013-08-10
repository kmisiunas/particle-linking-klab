package track.corrections

import org.scalatest.FunSuite
import com.misiunas.klab.track.ParticleTrack
import com.misiunas.klab.track.geometry.position.Pos
import com.misiunas.klab.track.assemblies.TrackAssembly
import com.misiunas.klab.track.geometry.Channel
import com.misiunas.klab.track.corrections.Filter

/**
 * User: karolis@misiunas.com
 * Date: 06/08/2013
 * Time: 02:09
 */
class FilterTest extends FunSuite {

  val pt1 = ParticleTrack(1, List( Pos(10,0,1), Pos(11,1,1), Pos(12,1.1,1)))
  val pt2 = ParticleTrack(2, List( Pos(16,6,3), Pos(17,5,1), Pos(18,6,1), Pos(19,7,1), Pos(20,8,2), Pos(21,10,2) ))
  val pt3 = ParticleTrack(3, List( Pos(20,5,3), Pos(21,4,1), Pos(23,3,1), Pos(24,2,2),   Pos(25,1.2,1) ))
  val pt4 = ParticleTrack(4, List( Pos(35,10,3), Pos(36,9,1), Pos(37,8,1), Pos(38,7,2),   Pos(39,6,1), Pos(40,5,1), Pos(41,4,1), Pos(42,3,1),Pos(43,2,1),Pos(44,1,1),Pos(45,3,1),Pos(46,1.2,1) ))
  val pt5 = ParticleTrack(5, List( Pos(50,4,1), Pos(51,3,1), Pos(52,2,1), Pos(53,3,1), Pos(54,4,2) ))
  val pt6 = ParticleTrack(6, List( Pos(50,101,1), Pos(51,100,1) ))


  val ta = TrackAssembly( List(pt1,pt2,pt3,pt4,pt5,pt6), "Test Experiment")

  val channel = Channel.simpleAlongX(2,8,6)

  test("Filter.bySize") {
    assert(Filter.bySize(4,8)(ta).toSet == Set(pt2,pt3,pt5))
  }

  test("Filter.byLocation") {
    assert(Filter.byLocation(channel)(ta).toSet == Set(pt2,pt3,pt4,pt5))
  }

  test("Filter.byContinuity") {
    assert(Filter.byContinuity(channel)(ta).toSet == Set(pt1,pt4,pt6))
  }

  test("Filter.byProximity") {
    assert(Filter.byProximity(4)(ta).toSet == Set(pt1,pt4,pt5,pt6), "filter gives: " + Filter.byProximity(4)(ta).toSet)
  }

}
