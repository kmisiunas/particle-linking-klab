package klab.track

import klab.track.infreastructure.tracks.{Track, TrackInfo}
import scala.collection.mutable.ListBuffer
import klab.track.geometry.position.Pos

/**
 * == Builder for ParticleTracks ==
 *
 * A special builder for ParticleTracks that can buffer an incoming stream and interpret it.
 *
 * Versions:
 *  - v3 - initial release, compatible with ParticleTrack v3
 *
 * User: karolis@misiunas.com
 * Date: 20/08/2013
 * Time: 14:33
 */
class TrackBuilder private (var id: Int,
                            val buffer: ListBuffer[Pos],
                            var units: List[String] = List("frame","px_x", "px_y", "px_z"),
                            var experiment: String = "",
                            var comment: String = "",
                            var time: Long = 0) extends Track with TrackInfo {

  // --------- Inherited Implementations ----------------

  def list: List[Pos] = buffer.toList
  def apply(i: Int): Pos = buffer.apply(i)
  def size: Int = buffer.size
  def isEmpty: Boolean = buffer.isEmpty
  var range: _root_.klab.track.STRange = (Pos(0, 0).toLQPos, Pos(0, 0).toLQPos)
  def version: Int = TrackBuilder.version

  // --------- New methods ----------

  /** add a position to the buffer - indirect it will update range parameters as well */
  def append(p: Pos): TrackBuilder = {
    updateRange(p)
    buffer.append(p)
    this
  }


  def toParticleTrack: ParticleTrack = {
    if (experiment.isEmpty) throw new RuntimeException("Please label the experiment with buffer.experiment_=\"name\"")
    if (time == 0) time = System.currentTimeMillis()
    ParticleTrack(id, list, units, experiment, comment, time)
  }

  // -------- Helper Methods ---------

  protected def updateRange(p: Pos): Unit = {
    if (!range._1.isAccurate) range = (p, range._2)
    if (!range._2.isAccurate) range = (range._1, p)
    range = (compose(range._1, p, Math.min), compose(range._2, p, Math.max))

    def compose(p1: Pos, p2: Pos, f: (Double, Double) => Double): Pos =
      Pos(p1.list.zip(p2.list).map(v => f(v._1,v._2) ))
  }

}

object TrackBuilder {

  final val version = 3

  /** it is a buffer thus make constructor slim */
  def apply(id: Int = getNewId()): TrackBuilder = new TrackBuilder(id, ListBuffer[Pos]())

  var lastUsedId: Int = 0 // store id accumulator for automatic id generation

  /** get next id */
  def getNewId(): Int = {lastUsedId = lastUsedId + 1; lastUsedId}
}
