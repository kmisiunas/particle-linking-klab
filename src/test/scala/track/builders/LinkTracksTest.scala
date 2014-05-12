package track.builders

/**
 * Created by kmisiunas on 10/04/2014.
 */
import collection.mutable.Stack
import org.scalatest._
import klab.track.builders.LinkTracks
import track.TestTracks

class LinkTracksTest extends FlatSpec with Matchers {

  "linkTwoTracks()" should "connect two tracks" in {
    val t = LinkTracks.linkTwoTracks(TestTracks.t1 , TestTracks.t2)
    t.isTimeOrdered should be (true)
    t.head.t should be (1)
    t.last.t should be (611)

    t(299).t should be (300)
    t(300).t should be (301)
    t(309).t should be (310)
    t(310).t should be (311)

    t.atTime(300).nonEmpty should be (true)
    t.atTime(301).nonEmpty should be (true)
    t.atTime(310).nonEmpty should be (true)
    t.atTime(311).nonEmpty should be (true)
  }

  it should "use LQPos connectors for new path" in {
    val t = LinkTracks.linkTwoTracks(TestTracks.t1 , TestTracks.t2)
    t.atTime(300).get.isAccurate should be (true)
    t.atTime(301).get.isAccurate should be (false)
    t.atTime(310).get.isAccurate should be (false)
    t.atTime(311).get.isAccurate should be (true)
  }



}