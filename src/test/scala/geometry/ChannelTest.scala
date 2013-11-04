package geometry

import org.scalatest.FunSuite
import klab.track.geometry.position.Pos
import klab.track.geometry.Channel
import com.misiunas.geoscala.volumes.BoxXY

/**
 * User: karolis@misiunas.com
 * Date: 23/09/2013
 * Time: 15:55
 */
class ChannelTest extends FunSuite {


  test("Channel.simpleAlongX") {
    val channel = Channel.simpleAlongX(2,8)
    assert(channel.length == 6)
    assert(channel.isWithin(Pos(0,2)))
    assert(channel.isWithin(Pos(0,8)))
    assert(channel.isWithin(Pos(0,5.1,1)), "ups! BoxXY = "+channel.geometry)
    assert(channel.isWithin(Pos(0,4.9,-1)))
    assert(!channel.isWithin(Pos(0,1)))
    assert(!channel.isWithin(Pos(0,9)))

  }


}