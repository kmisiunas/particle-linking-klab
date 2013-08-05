package track.analysis

import org.scalatest.FunSuite
import com.misiunas.klab.track.ParticleTrack
import com.misiunas.klab.track.geometry.position.Pos
import com.misiunas.klab.track.assemblies.TrackAssembly
import com.misiunas.klab.track.analysis.Find

/**
 * User: karolis@misiunas.com
 * Date: 05/08/2013
 * Time: 21:45
 */
class FindTest extends FunSuite {

  val pt1 = ParticleTrack(1, List( Pos(10,1,1), Pos(11,1.3,1), Pos(12,1.2,1), Pos(13,1.1,1), Pos(14,2,2) ))
  val pt2 = ParticleTrack(2, List( Pos(12,2,3), Pos(13,1.3,1), Pos(14,1.2,1), Pos(15,1.1,1), Pos(16,2,2) )) //special
  val pt3 = ParticleTrack(3, List( Pos(20,2,3), Pos(21,1.3,1), Pos(23,1.1,1), Pos(24,2,2),   Pos(25,1.2,1) ))
  val pt4 = ParticleTrack(4, List( Pos(15,2,3), Pos(16,1.3,1), Pos(17,1.1,1), Pos(18,2,2),   Pos(19,1.2,1), Pos(20,1.2,1), Pos(21,1.2,1), Pos(22,1.2,1),Pos(23,1.2,1),Pos(23,1.3,1),Pos(24,1.2,1),Pos(25,1.2,1),Pos(26,1.2,1) ))

  val ta = TrackAssembly( List(pt1,pt2,pt3,pt4), "Test Experiment")

  test("Find.atTime( time )") {
    assert(Find.atTime(12)(ta).toSet == Set(pt1,pt2))
    assert(Find.atTime(24)(ta).toSet == Set(pt3,pt4))
  }

  test("Find.atTime( minTime, maxTime )") {
    assert(Find.atTime(11.5,12.2)(ta).toSet == Set(pt1,pt2))
    assert(Find.atTime(15,21)(ta).toSet == Set(pt2,pt3,pt4))
  }

  test("Find.alignTwoTracks( ParticleTrack, ParticleTrack )") {
    assert(Find.alignTwoTracks(pt1,pt2) == List(Pos(12,1.2,1), Pos(13,1.1,1), Pos(14,2,2)).zip( List(Pos(12,2,3), Pos(13,1.3,1), Pos(14,1.2,1)) ) )
  }

}
