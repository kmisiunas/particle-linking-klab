package test.scala

import org.scalatest.{Matchers, FlatSpec, FunSuite}
import klab.track.Track
import klab.track.geometry.position.Pos
import track.TestTracks


/**
 * User: karolis@misiunas.com
 * Date: 13/07/2013
 * Time: 18:45
 */
class TrackTest extends FlatSpec with Matchers {

  "apply()" should "give Pos at that point in the list starting from 0 index" in {
    TestTracks.t3.apply(1) should be (Pos(11,1.3,-1))
  }

  "size()" should "should give number of elements stored in the track" in {
    TestTracks.t1.size should be (300)
  }

  "timeRange()" should "give a time range for the Track" in {
    TestTracks.t1.timeRange should be ( (1.0,300.0) )
  }

  "range()" should "give a range over which the track veries" in {
    TestTracks.t3.range should be ( (Pos(10,0,-1), Pos(14,2,1)) )
  }

  "timeOrder()" should "check if the list is time ordered" in {
    // currentlly only checks if good.
    TestTracks.t1.isTimeOrdered should be ( true )
  }

  "atTime()" should "should return a Pos at that time" in {
    TestTracks.t1.atTime(1)   should be ( Option(Pos(1, 4.135903062765138e-18)) )
    TestTracks.t1.atTime(3)   should be ( Option(Pos(3, -0.018767030536265567)) )
    TestTracks.t1.atTime(3.3) should be ( Option(Pos(3, -0.018767030536265567)) )
    TestTracks.t1.atTime(3.7) should be ( Option(Pos(3, -0.018767030536265567)) )
    TestTracks.t1.atTime(0)   should be ( None )
    TestTracks.t1.atTime(0.1) should be ( None )
    TestTracks.t1.atTime(302) should be ( None )
  }

  "atTimeIdx()" should "should return a Pos at that time" in {
    TestTracks.t1.atTimeIdx(1)   should be ( 0 )
    TestTracks.t1.atTimeIdx(3)   should be ( 2 )
    TestTracks.t1.atTimeIdx(3.3) should be ( 2 )
    TestTracks.t1.atTimeIdx(3.7) should be ( 2 )
    TestTracks.t1.atTimeIdx(100) should be ( 99 )
    TestTracks.t1.atTimeIdx(300) should be ( 299 )
    TestTracks.t1.atTimeIdx(0)   should be ( -1 )
    TestTracks.t1.atTimeIdx(0.1) should be ( -1 )
    TestTracks.t1.atTimeIdx(302) should be ( -1 )
  }

  }
