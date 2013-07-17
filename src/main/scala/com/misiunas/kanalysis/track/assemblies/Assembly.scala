package com.misiunas.kanalysis.track.assemblies

import com.misiunas.kanalysis.track.formating.CompatibleWithJSON
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import com.misiunas.kanalysis.track.ParticleTrack
import com.misiunas.kanalysis.track.position.Pos


/**
 * == An Assembly of tracks - trait infrastructure ==
 *
 *
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 20:36
 */
abstract class Assembly (experiment:String, comment: String, time: Long) extends CompatibleWithJSON[Assembly]{

  // ---------------- Some abstract methods --------------

  /** number of tracks in this assembly */
  def size : Int
  /** the version of the class */
  def version = 2
  /** ordered list of tracks */
  def list : List[ParticleTrack]
  /** convert to mutable */
  def toMutable : TrackAssemblyM
  /** convert to immutable */
  def toImmutable : TrackAssembly
  /** access the map where the data is stored */
  def listMap : collection.Map[Int, ParticleTrack]





  // ---------------- General implemented methods --------

  /** access the ParticleTrack with specific ID */
  def apply(id : Int) : ParticleTrack = listMap(id)

  /** Check is assembly conforms to desired specs */
  def qualityCheck : Boolean = {
    // check for common units
    val l = list
    l.forall(_.units == l.head.units)
  }

  /** An map for converting this to JSON structure */
  private def json =
    ("TrackAssembly" ->
      ("number" -> size) ~
        ("experiment" -> experiment) ~
        ("time" -> time) ~
        ("comment" -> comment) ~
        ("version" -> version) ~
        ("array_format" -> List("time_stamp","x","y","z")) ~
        ("units" -> list.head.units) ~
        ("ParticleTrack" ->
          list.map { pt =>
            (("id" -> pt.id) ~
              ("positions" -> pt.positions.map(_.list)))
          }))

  def toJSON : String = pretty(render(json))

  /** general method for turning the JSON file into data - preparation as there is no constructors yet */
  protected def fromJSONprep(st: String) : List[ParticleTrack] = {
    implicit val formats = net.liftweb.json.DefaultFormats
    val code = parse(st)
    if((code \\ "version").extract[Int] > ParticleTrack.version) throw new Exception("Warning: the ParticleTrack file is version "+(code \\ "version").extract[Int] + ", while the current version is"+version)
    val experiment = (code \\ "experiment").extract[String]
    val comment = (code \\ "comment").extract[String]
    val time = (code \\ "time").extract[Long]
    val units = (code \\ "units").extract[List[String]]
    (code \\ "ParticleTrack").extract[List[JObject]].map( sub => ParticleTrack(
      id = (sub \\ "id").extract[Int],
      list = (sub \\ "positions").extract[List[List[Double]]].map(Pos(_)),
      units, experiment, comment, time ) )
  }

  // ---------------- Manipulate methods --------------

}
