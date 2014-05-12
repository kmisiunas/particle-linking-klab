package track.corrections

import org.scalatest.FunSuite
import klab.track.Track
import klab.track.geometry.position.Pos
import klab.track.assemblies.TrackAssembly
import klab.track.geometry.Channel
import scala.Predef._
import klab.track.corrections.specialised.Continuum

/**
 * User: karolis@misiunas.com
 * Date: 23/07/2013
 * Time: 22:55
 */
class ContinuumTest extends FunSuite {

  println("ContinuumTest")


  val pt1 = Track(1, List( Pos(10,0,1), Pos(11,1,1), Pos(12,2,1), Pos(13,3,1), Pos(14,4,2) ))
  val pt2 = Track(2, List( Pos(16,6,3), Pos(17,5,1), Pos(18,6,1), Pos(19,7,1), Pos(20,8,2), Pos(21,10,2) ))
  val pt3 = Track(3, List( Pos(20,5,3), Pos(21,4,1), Pos(23,3,1), Pos(24,2,2),   Pos(25,1.2,1) ))
  val pt4 = Track(4, List( Pos(35,10,3), Pos(36,9,1), Pos(37,8,1), Pos(38,7,2),   Pos(39,6,1), Pos(40,5,1), Pos(41,4,1), Pos(42,3,1),Pos(43,2,1),Pos(44,1,1),Pos(45,3,1),Pos(46,1.2,1) ))
  val pt5 = Track(5, List( Pos(50,4,1), Pos(51,3,1), Pos(52,2,1), Pos(53,3,1), Pos(54,4,2) ))

  val ta = TrackAssembly( List(pt1,pt2,pt3,pt4,pt5), "Test Experiment")

  val channel = Channel.alongX("test", 2,8)

  ignore("Continuum.pairUp") {
    assert(true)
  }

  ignore("Continuum.qualityCheck(ta: Assembly)") {
    assert(true)
  }

  ignore("Continuum.find(withinChannel: GeoVolume)") {
    assert( Continuum.find(channel)(ta) == (Set(pt2,pt3), Set(pt5), Set(pt1)))
  }

}


