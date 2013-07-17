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
              ("positions" -> pt.positions.map(_.list)))
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


}
