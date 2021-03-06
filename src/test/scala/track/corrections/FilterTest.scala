package track.corrections

import org.scalatest.FunSuite
import klab.track.Track
import klab.track.geometry.position.Pos
import klab.track.assemblies.TrackAssembly
import klab.track.geometry.Channel
import klab.track.corrections.Filter

/**
 * User: karolis@misiunas.com
 * Date: 06/08/2013
 * Time: 02:09
 */
class FilterTest extends FunSuite {

  println("FilterTest")

  val pt1 = Track(1, List( Pos(10,0,1), Pos(11,1,1), Pos(12,1.1,1)))
  val pt2 = Track(2, List( Pos(16,6,3), Pos(17,5,1), Pos(18,6,1), Pos(19,7,1), Pos(20,8,2), Pos(21,10,2) ))
  val pt3 = Track(3, List( Pos(20,5,3), Pos(21,4,1), Pos(23,3,1), Pos(24,2,2),   Pos(25,1.2,1) ))
  val pt4 = Track(4, List( Pos(35,10,3), Pos(36,9,1), Pos(37,8,1), Pos(38,7,2),   Pos(39,6,1), Pos(40,5,1), Pos(41,4,1), Pos(42,3,1),Pos(43,2,1),Pos(44,1,1),Pos(45,3,1),Pos(46,1.2,1) ))
  val pt5 = Track(5, List( Pos(50,4,1), Pos(51,3,1), Pos(52,2,1), Pos(53,3,1), Pos(54,4,2) ))
  val pt6 = Track(6, List( Pos(50,101,1), Pos(51,100,1) ))


  val ta = TrackAssembly( List(pt1,pt2,pt3,pt4,pt5,pt6), "Test Experiment")

  val channel = Channel.alongX("test", 2,8)

  test("Filter.bySize") {
    assert(Filter.bySize(4,8)(ta).toSet == Set(pt2,pt3,pt5))
  }

  ignore("Filter.byLocation") {
    assert(Filter.byLocation(channel)(ta).toSet == Set(pt2,pt3,pt4,pt5))
  }

  ignore("Filter.byContinuity") {
    assert(Filter.byContinuity(channel)(ta).toSet == Set(pt1,pt4,pt6))
  }

  test("Filter.byProximity") {
    assert(Filter.byProximity(4)(ta).toSet == Set(pt1,pt4,pt5,pt6), "filter gives: " + Filter.byProximity(4)(ta).toSet)
  }

}
