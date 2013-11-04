package klab.track

import org.joda.time.DateTime
import klab.track.geometry.position.Pos
import klab.track.infreastructure.tracks.{TrackInfo, Track, ConstructorTrack}


/**
 * ==Particle Track==
 *
 * -------------------------------------------------------
 *
 * A special object for storing particle track.
 * It should contain: id, {time,x,y,z} positions
 * also any additional information about the track.
 *
 * This is a basic unit for all analysis. The methods in this class are
 * optimised for performance and ease of use.
 *
 *
 * Versions:
 *  - v1 - initial release (scala 2.10)
 *  - v2 - the Track was made into immutable object, added structure, JSON update
 *  - v3 - separated most functionality into inheritable traits. Slight syntax improvements. toCSV added.
 *
 *  TODO: unique objects just like String
 *
 *  @author karolis@misiunas.com,
 *  Date: 11/07/2013,
 *  Time: 14:28
 */
class ParticleTrack private (
                    val id: Int, // ID of the particle
                    val list: List[Pos], // list of positions
                    val units: List[String], // units of the particle track!
                    val experiment:String, // the experiment title
                    val comment: String, // store additional information
                    val time:Long) // the time stamp!
  extends ConstructorTrack[ParticleTrack] with Track with TrackInfo {

  /** A private constructor method */
  override protected def makeFrom(id: Int, list: List[Pos], units: List[String],
                       experiment: String, comment: String, time: Long): ParticleTrack =
    new ParticleTrack(id, list, units, experiment, comment, time)

  // ############## Methods ##################

  override def equals(other: Any): Boolean  = other match {
    case that: ParticleTrack => {
      //that.version == version && // not necessary for equality
      that.id == id &&
      //that.time == time && // no comparison in time as it ussaly creation time!
      that.experiment == experiment &&
      that.comment == comment &&
      that.units == units &&
      that.list == list }
    case _ => false
  }

  // ############## Other ####################

  override def toString : String = "ParticleTrack(id="+id+", size="+ size +")"

  /** version of particle track object */
  def version = ParticleTrack.version
}

/**
 * Object for static function access
 */
object ParticleTrack {

  /** the version of the particle track */
  val version = 3

  /** a cheat! - for accessing .make() method */
  private val Maker  = new ParticleTrack (-1,Nil,Nil,"","",0)

  def apply(id: Int,
            list: List[Pos],
            units: List[String] = List("frame","px_x", "px_y", "px_z"),
            experiment: String = "Experiment_on_"+ DateTime.now().toLocalDate.toString,
            comment: String = "",
            time: Long = System.currentTimeMillis()): ParticleTrack =
    Maker.make(id,list,units,experiment, comment, time)

  def apply(id: Int, seq: Iterable[Any]): ParticleTrack = {
    if (seq.isEmpty) throw new RuntimeException("Can't make ParticleTrack out of empty list")
    seq.head match {

      case _:Pos => { // form a new particle track
        val l = seq.asInstanceOf[ Iterable[Pos]] // not very good code
        apply(id = id, list = l.toList, comment = "") // todo: this code might be interpreted recursevley
      }

      case _:ParticleTrack => { // join up two tracks algorithm
        val l = seq.asInstanceOf[ Iterable[ParticleTrack] ] // not very good code
        ParticleTrack(id,
          l.map( _.list).flatten.toList,
          l.head.units,
          l.head.experiment,
          "Joined particle track from tracks = {" + l.map(_.id).mkString(",")+"}",
          l.head.time) // time ordering will be applied automatically
      }

      case _ => throw new RuntimeException("Could not convert sequence into ParticleTrack: " + seq)
    }
  }

  def apply(json: String): ParticleTrack = Maker.fromJSON(json)

}