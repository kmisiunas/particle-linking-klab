package com.misiunas.kanalysis.track

import org.joda.time.DateTime
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import com.misiunas.kanalysis.track.units.HasUnits
import com.misiunas.kanalysis.track.formating.CompatibleWithJSON
import com.misiunas.kanalysis.track.position.Pos


/**
 * ==Particle Track==
 *
 * -------------------------------------------------------
 *
 * A special object for storing particle track.
 * It should contain: id, {time,x,y,z} positions
 * also any additional information about the track.
 *
 *
 * Versions:
 *  - v1 - initial release (scala 2.10)
 *  - v2 - the Track was made into immutable object, added structure, JSON update
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
                    val units: List[String] = List("frame","px_x", "px_y", "px_z"),
                    val experiment:String = "Experiment_on_"+ DateTime.now().toLocalDate.toString, // the experiment title
                    val comment: String = "",
                    val time:Long = System.currentTimeMillis())
 extends OrderedTrack with HasUnits with CompatibleWithJSON[ParticleTrack]{

  // ############## Methods ##################

  override def equals(other: Any): Boolean  = other match {
    case that: ParticleTrack => {
      that.version == version &&
      that.id == id &&
      that.time == time &&
      that.experiment == experiment &&
      that.comment == comment &&
      that.units == units &&
      that.list == list }
    case _ => false
  }


  def positions = list //other name for the same thing


  // ############## Other ####################

  /** An map for converting this to JSON structure */
  private def json =
    ("ParticleTrack" ->
      ("id" -> id) ~
      ("experiment" -> experiment) ~
      ("time" -> time) ~
      ("comment" -> comment) ~
      ("version" -> version) ~
      ("units" -> units) ~
      ("positions" ->list.map(_.list))
    )

  def toJSON : String = pretty(render(json))

  def fromJSON(st: String) : ParticleTrack = ParticleTrack.fromJSON(st)

  override def toString : String = "ParticleTrack(id="+id+")"

  def apply(i: Int): Pos = list.apply(i)
  def isEmpty = list.isEmpty
  lazy val size: Int = list.size

  /** version of particle track object */
  def version = ParticleTrack.version

  // ############## Modification method - slow because it is immutable object ####################

  def changeId(newId : Int) : ParticleTrack =
    ParticleTrack(newId,list,units,experiment,comment,time)

  def changePositions(newList : List[Pos]) : ParticleTrack =
    ParticleTrack(id,newList,units,experiment,comment,time)

  def changeComment(newComment :String) : ParticleTrack =
    ParticleTrack(id,list,units,experiment,newComment,time)

  def changeUnits(newList: List[Pos], newUnits: List[String]) : ParticleTrack =
    ParticleTrack(id,newList,newUnits,experiment,comment,time)

  def timeOrder : ParticleTrack = changePositions(list.sortWith(_.t < _.t))
}

/**
 * Object for static function access
 */
object ParticleTrack {

  /** the version of the particle track */
  val version = 2

  def apply(id: Int, list: List[Pos], units: List[String], experiment: String, comment: String, time: Long) : ParticleTrack =
    new ParticleTrack(id,list,units,experiment, comment, time)
  def apply(id: Int, list: List[Pos], units: List[String],  experiment: String) : ParticleTrack =
    new ParticleTrack(id,list,units,experiment)
  def apply(id: Int, list: List[Pos], units: List[String]) : ParticleTrack =
    new ParticleTrack(id,list,units)
  def apply(id: Int, list: List[Pos]) : ParticleTrack = new ParticleTrack(id,list)
  def apply(json: String) : ParticleTrack = ParticleTrack.fromJSON(json)

  def fromJSON(st: String) : ParticleTrack = {
    implicit val formats = net.liftweb.json.DefaultFormats
    val code = parse(st)
    if((code \\ "version").extract[Int] != ParticleTrack.version) throw new Exception("Warning: the ParticleTrack file is version "+(code \\ "version").extract[Int] + ", while the current version is"+version)
    return ParticleTrack(
      id = (code \\ "id").extract[Int],
      list = (code \\ "positions").extract[List[List[Double]]].map(Pos(_)),
      units = (code \\ "units").extract[List[String]],
      experiment = (code \\ "experiment").extract[String],
      comment = (code \\ "comment").extract[String],
      time = (code \\ "time").extract[Long] )
  }
}