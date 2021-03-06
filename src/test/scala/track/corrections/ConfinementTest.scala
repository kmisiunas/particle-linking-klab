package track.corrections

import org.scalatest.FunSuite
import klab.track.Track
import klab.track.geometry.position.Pos
import klab.track.assemblies.TrackAssembly
import klab.track.geometry.Channel
import klab.track.corrections.specialised.Confinement
import Confinement.ResOverlap

/**
 * User: karolis@misiunas.com
 * Date: 08/08/2013
 * Time: 02:29
 */
class ConfinementTest extends FunSuite {

  println("ConfinementTest")

  val pt1 = Track(1, List( Pos(10,0,1), Pos(11,1,1), Pos(12,2,1), Pos(13,3.1), Pos(14,4,2), Pos(15,5), Pos(16,5.5),Pos(17,6), Pos(18,6.5)))
  val pt2 = Track(2, List( Pos(11,5), Pos(12,4), Pos(13,3), Pos(14,2), Pos(15,3), Pos(16,4), Pos(17,6.5), Pos(18,8) ))
  val pt3 = Track(3, List( Pos(9,4), Pos(10,3), Pos(11,2), Pos(12,1),Pos(13,0) ))
  val pt4 = Track(4, List( Pos(35,10,3), Pos(36,9,1), Pos(37,8,1), Pos(38,7,2),   Pos(39,6,1), Pos(40,5,1), Pos(41,4,1), Pos(42,3,1),Pos(43,2,1),Pos(44,1,1),Pos(45,3,1),Pos(46,1.2,1) ))
  val pt5 = Track(5, List( Pos(50,4,1), Pos(51,3,1), Pos(52,2,1), Pos(53,3,1), Pos(54,4,2) ))
  val pt6 = Track(1, List( Pos(10,0,1), Pos(11,1,1), Pos(12,2,1),Pos(13,3),  Pos(14,2),   Pos(15,3), Pos(16,4), Pos(17,6.5), Pos(18,8) ))
  val pt7 = Track(2, List(              Pos(11,5),   Pos(12,4),  Pos(13,3.1),Pos(14,4,2), Pos(15,5), Pos(16,5.5),Pos(17,6), Pos(18,6.5) ))

  val ta = TrackAssembly( List(pt1,pt2,pt3,pt4,pt5), "Test Experiment")

  test("Confinement.swapAtOverlap") {
    val r1 = Confinement.findOverlaps(_.x)(ta)(1)
    assert(r1.atTime == 13.0)
    val fixed = Confinement.swapAtOverlap(r1)
    assert(fixed(0).list == pt6.list)
    assert(fixed(1).list == pt7.list)
  }

  test("Confinement.findOverlaps") {
    assert(
      Confinement.findOverlaps(_.x)(ta).toSet ==
        Set(new ResOverlap(pt1,pt2, 13.0), new ResOverlap(pt1,pt2, 17.0),new ResOverlap(pt1,pt3, 12.0)) )
  }


}