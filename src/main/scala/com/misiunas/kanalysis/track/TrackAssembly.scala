package com.misiunas.kanalysis.track

import scala.collection.mutable.Buffer
import net.liftweb.json.JsonAST.{JDouble, JField}
import net.liftweb.json.JsonDSL._
import net.liftweb.json._

/**
 * A class for storing an managing tracks
 *
 * Very early version of this class
 *
 * User: karolis@misiunas.com
 * Date: 12/07/2013
 * Time: 15:50
 */
class TrackAssembly (val list: Buffer[ParticleTrack] = Buffer()) {

  // ############## Other ####################

  /** An map for converting this to JSON structure */
  private def json =
    ("TrackAssembly" ->
        ("number" -> list.size) ~
        ("experiment" -> list.head.experiment) ~
        ("time" -> list.head.time) ~
        ("comment" -> list.head.comment) ~
        ("version" -> list.head.version) ~
        ("array_format" -> List("time_stamp","x","y","z")) ~
        ("units" -> list.head.units) ~
        ("ParticleTrack" ->
          list.map { pt =>
            (("id" -> pt.id) ~
              ("positions" -> pt.positions))
          }))

  def toJSON : String = pretty(render(json))

  def mkString = toJSON

}

/**
 * Object for static function access
 */
object TrackAssembly {

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
