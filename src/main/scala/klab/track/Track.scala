package klab.track

import org.joda.time.DateTime
import klab.track.geometry.position.Pos
import klab.track.infreastructure.tracks.{TrackInfo, ConstructorTrack}
import breeze.linalg.DenseMatrix


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
 * TODO:
 *  - hash key equals, compute a key that will be quicker to compare to other Particle Track
 *  - copy
 *
 * Features:
 *  - Fast equals() method via reference checking
 *  - immutable object
 *
 *
 * Versions:
 *  - v1 - initial release (scala 2.10)
 *  - v2 - the Track was made into immutable object, added structure, JSON update
 *  - v3 - separated most functionality into inheritable traits. Slight syntax improvements. toCSV added.
 *  - v0.1.6 - quick equals method via reference check!
 *
 * Version: 0.1.6
 * Author karolis@misiunas.com,
 * Date: 11/07/2013,
 */
class Track private (
                    val id: Int, // ID of the particle
                    val list: List[Pos], // list of positions
                    val units: List[String], // units of the particle track!
                    val experiment:String, // the experiment title
                    val comment: String, // store additional information
                    val time:Long) // the time stamp!
  extends ConstructorTrack[Track] with klab.track.infreastructure.tracks.TrackBase with TrackInfo {

  /** A private constructor method */
  override protected def makeFrom(id: Int, list: List[Pos], units: List[String],
                       experiment: String, comment: String, time: Long): Track =
    new Track(id, list, units, experiment, comment, time)

  // ############## Methods ##################

  override def equals(other: Any): Boolean  = other match {
    case that: Track => {
      if (that eq this) true else { // a quick reference check
        //that.version == version && // not necessary for equality
        that.id == id &&
        //that.time == time && // no comparison in time as it ussaly creation time!
        that.experiment == experiment &&
        //that.comment == comment &&  // only care about the data!
        that.units == units &&
        that.list == list
      }
    }
    case _ => false
  }

  /** customisable copy - a preferred way of modifying particle tracks */
  def copy( id: Int = this.id,
            list: List[Pos] = this.list,
            units: List[String] = this.units,
            experiment:String = this.experiment,
            comment: String = this.comment,
            time:Long = this.time): Track = Track(id, list, units, experiment, comment, time)


  // ############## Other ####################

  override def toString : String = "Track(id="+id+", size="+ size +")"

  /** version of particle track object */
  def version = Track.version

  /** method fro returning matrix - breeze.
    * removes LQPos */
  def toMatrix: DenseMatrix[Double] = {
    val export = list.filter(_.isAccurate)
    val m = DenseMatrix.zeros[Double](export.length, 4)
    export.zipWithIndex
      .foreach( p => {
        m(p._2, 0) = p._1(0)
        m(p._2, 1) = p._1(1)
        m(p._2, 2) = p._1(2)
        m(p._2, 3) = p._1(3)
      })
    return m
  }

  /** marks tracks that are not desired as LQPos */
  def filterToLQPos(f: Pos => Boolean): Track =
    this.copy(
      list = list.map( pos => if (f(pos)) pos else pos.toLQPos )
    )

}

/**
 * Object for static function access
 */
object Track {

  /** the version of the particle track */
  val version = 3

  /** a cheat! - for accessing .make() method */
  private val Maker  = new Track (-1,Nil,Nil,"","",0)

  def apply(id: Int,
            list: List[Pos],
            units: List[String] = List("frame","px_x", "px_y", "px_z"),
            experiment: String = "Experiment_on_"+ DateTime.now().toLocalDate.toString,
            comment: String = "",
            time: Long = System.currentTimeMillis()): Track =
    Maker.make(id,list,units,experiment, comment, time)

  def apply(id: Int, seq: Iterable[Any]): Track = {
    if (seq.isEmpty) throw new RuntimeException("Can't make Track out of empty list")
    seq.head match {

      case _:Pos => { // form a new particle track
        val l = seq.asInstanceOf[ Iterable[Pos]] // not very good code
        apply(id = id, list = l.toList, comment = "") // todo: this code might be interpreted recursevley
      }

      case _:Track => { // join up two tracks algorithm
        val l = seq.asInstanceOf[ Iterable[Track] ] // not very good code
        Track(id,
          l.map( _.list).flatten.toList,
          l.head.units,
          l.head.experiment,
          "Joined particle track from tracks = {" + l.map(_.id).mkString(",")+"}",
          l.head.time) // time ordering will be applied automatically
      }

      case _ => throw new RuntimeException("Could not convert sequence into Track: " + seq)
    }
  }

  def apply(json: String): Track = Maker.fromJSON(json)

}