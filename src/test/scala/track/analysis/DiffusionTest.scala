package track.analysis

import org.scalatest.FunSuite
import klab.track.ParticleTrack
import klab.track.geometry.position.Pos
import klab.track.assemblies.TrackAssembly
import klab.track.analysis.Find

/**
 * Created with IntelliJ IDEA.
 * User: kmisiunas
 * Date: 04/11/2013
 * Time: 11:53
 * To change this template use File | Settings | File Templates.
 */
class DiffusionTest extends FunSuite {

  println("FindTest")

  val pt1 = ParticleTrack(1, List( Pos(10,1,1), Pos(11,1.3,1), Pos(12,1.2,1), Pos(13,1.1,1), Pos(14,2,2) ))
  val pt2 = ParticleTrack(2, List( Pos(12,2,3), Pos(13,1.3,1), Pos(14,1.2,1), Pos(15,1.1,1), Pos(16,2,2) )) //special
  val pt3 = ParticleTrack(3, List( Pos(20,2,3), Pos(21,1.3,1), Pos(23,1.1,1), Pos(24,2,2),   Pos(25,1.2,1) ))
  val pt4 = ParticleTrack(4, List( Pos(15,2,3), Pos(16,1.3,1), Pos(17,1.1,1), Pos(18,2,2),   Pos(19,1.2,1), Pos(20,1.2,1), Pos(21,1.2,1), Pos(22,1.2,1),Pos(23,1.2,1),Pos(23,1.3,1),Pos(24,1.2,1),Pos(25,1.2,1),Pos(26,1.2,1) ))

  val ta = TrackAssembly( List(pt1,pt2,pt3,pt4), "Test Experiment")

  test("Diffusion. implement me!") {
    assert(false)
  }
}
