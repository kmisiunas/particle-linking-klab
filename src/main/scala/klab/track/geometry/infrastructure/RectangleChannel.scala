package klab.track.geometry.infrastructure

import klab.track.geometry.Channel
import com.misiunas.geoscala.vectors.Vec
import com.misiunas.geoscala.Point
import com.misiunas.geoscala.volumes.{BoxXY, Volume}

/**
 * == An implementation of Channel ==
 *
 * User: kmisiunas
 * Date: 11/12/2013
 */
class RectangleChannel(override val name: String, // the name of the channel (id)
                       override val direction: Vec,
                       override val middle: Point,
                       override val size: Vec, // assuming aligned along x. y - width and z - height
                       override val inletLength: Double,
                       override val outletLength: Double)
  extends Channel (name, direction, middle, size, inletLength, outletLength) {

  lazy val inlet: Volume = {
    val size = this.size.copy(x = inletLength)
    val offset = direction * ( (inletLength - length) / 2 )
    BoxXY( size * (-0.5), size * 0.5 )
      .rotateAroundZ( Vec.x.angle(direction) )
      .transform( _ + middle + offset)
  }

  lazy val outlet: Volume = {
    val size = this.size.copy(x = this.size.x - outletLength - inletLength)
    BoxXY( size * (-0.5), size * 0.5 )
      .rotateAroundZ( Vec.x.angle(direction) )
      .transform( _ + middle)
  }

  lazy val volume: Volume = BoxXY( size * (-0.5), size * 0.5 )
                              .rotateAroundZ( Vec.x.angle(direction) )
                              .transform( _ + middle)

  lazy val innerVolume: Volume = {
    val size = this.size.copy(x = outletLength)
    val offset = -direction * ( (outletLength - length) / 2 )
    BoxXY( size * (-0.5), size * 0.5 )
      .rotateAroundZ( Vec.x.angle(direction) )
      .transform( _ + middle + offset)
  }
}


object RectangleChannel {

  def apply(name: String, direction: Vec, middle: Point, size: Vec,
            inletLength: Double, outletLength: Double): RectangleChannel =
    new RectangleChannel(name, direction, middle, size, inletLength, outletLength)

}