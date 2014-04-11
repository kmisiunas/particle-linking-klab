package geometry

import org.scalatest.FunSuite
import klab.track.geometry.position.Pos
import klab.track.geometry.Channel
import com.misiunas.geoscala.volumes.BoxXY
import com.misiunas.geoscala.Point
import com.misiunas.geoscala.vectors.Vec

/**
 * User: karolis@misiunas.com
 * Date: 23/09/2013
 * Time: 15:55
 */
class ChannelTest extends FunSuite {


  ignore("Channel.alongX") {
    val channel = Channel.alongX("test",2,6)
    assert(channel.length == 6)
    assert(channel.isWithin(Pos(0,2)))
    assert(channel.isWithin(Pos(0,8)))
    assert(channel.isWithin(Pos(0,5.1,1)), "ups! BoxXY = "+channel.volume)
    assert(channel.isWithin(Pos(0,4.9,-1)))
    assert(!channel.isWithin(Pos(0,1)))
    assert(!channel.isWithin(Pos(0,9)))
  }


  ignore("Channel.alongX #2") {
    val channel = Channel.alongX("test",0,100)
    assert(channel.direction == Vec.x)
    assert(channel.length == 100)
    assert(channel.width == 2*channel.length)
    val v =  BoxXY( Point(0.0, -100.0, 0.0),  Point(100.0, 100.0, 0.0))
    assert(channel.volume == v )
    val i = BoxXY( Point(0.0, -100.0, 0.0), Point(10.0, 100.0, 0.0))
    assert(channel.inlet == i)
    val o = BoxXY( Point(90.0, -100.0, 0.0), Point(100.0, 100.0, 0.0))
    assert(channel.outlet == o)
  }

  ignore("Channel.apply - channel at an angle!") {
    val channel = Channel.alongX("test",0,100)
    assert(channel.direction == Vec.x)
    assert(channel.length == 100)
    assert(channel.width == 2*channel.length)
    val v =  BoxXY( Point(0.0, -100.0, 0.0),  Point(100.0, 100.0, 0.0))
    assert(channel.volume == v )
    val i = BoxXY( Point(0.0, -100.0, 0.0), Point(10.0, 100.0, 0.0))
    assert(channel.inlet == i)
    val o = BoxXY( Point(90.0, -100.0, 0.0), Point(100.0, 100.0, 0.0))
    assert(channel.outlet == o)
  }


}