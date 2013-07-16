package com.misiunas.kanalysis.track

import org.joda.time.DateTime
import net.liftweb.json.JsonDSL._
import net.liftweb.json._


/**
 * ==Particle Track==
 *
 * -------------------------------------------------------
 *
 * A special object for storing particle track.
 * It should contain: id, {time,x,y,z} positions
 * also any additional information about the track.
 * Object should be backward compatible or indicate which
 * version it is using. Also functional manipulations should be implemented.
 *
 * Versions:
 *  - v1 - initial release (scala 2.10)
 *
 *  @author karolis@misiunas.com,
 *  Date: 11/07/2013,
 *  Time: 14:28
 */
class ParticleTrack(val id: Int, // ID of the particle
                    val experiment:String = "Experiment_on_"+ DateTime.now().toLocalDate.toString, // the experiment title
                    val time:Long = System.currentTimeMillis()) extends Serializable {


  /** version of particle track object */
  def version = ParticleTrack.version

  /** x,y,z coordinate of the particle */
  type Coordinate = List[Double]

  /** values are stored here */
  var positions: List[Coordinate] = Nil

  /** Array containing units of the Coordinates: {x,y,z,time} */
  var units: List[String] = List("frame","px_x", "px_y", "px_z")

  /** Comment about the track */
  var comment:String = ""


  // ############## Methods ##################

  override def equals(other: Any): Boolean  = other match {
    case that: ParticleTrack =>
      that.version == version &&
      that.id == id &&
      that.time == time &&
      that.experiment == experiment &&
      that.comment == comment &&
      that.units == units
      that.positions == positions
    case _ => false
  }


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
        ("positions" ->
          positions.map { p =>
            (("t" -> p(0)) ~
             ("x" -> p(1)) ~
             ("y" -> p(2)) ~
             ("z" -> p(3)))
          }))

  def toJSON : String = pretty(render(json))

  def mkString = toJSON

}

/**
 * Object for static function access
 */
object ParticleTrack {

  /** the version of the particle track */
  val version = 1

  /**
   * Method for constructing a ParticleTrack from a JSON text string
   * @param st
   * @return
   */
  def constructJSON(st: String) : ParticleTrack = {
    implicit val formats = net.liftweb.json.DefaultFormats
    val code = parse(st)
    if((code \\ "version").extract[Int] != ParticleTrack.version) throw new Exception("Warning: the ParticleTrack file is version "+(code \\ "version").extract[Int] + ", while the current version is"+version)
    val pt = new ParticleTrack(id = (code \\ "id").extract[Int],
      time = (code \\ "time").extract[Long],
      experiment = (code \\ "experiment").extract[String])
    // Extract mutable values and add to "pt"
    // extract the positions
    val pos = (code \\ "positions").extract[List[JObject]] // the position list
    pt.positions = for { obj <- pos
                         JField("t", JDouble(t)) <- obj
                         JField("x", JDouble(x)) <- obj
                         JField("y", JDouble(y)) <- obj
                         JField("z", JDouble(z)) <- obj
    } yield List(t,x,y,z)
    // extract units
    pt.units = (code \\ "units").extract[List[String]]
    // Extract extra comment
    pt.comment = (code \\ "comment").extract[String]
    return pt
  }
}