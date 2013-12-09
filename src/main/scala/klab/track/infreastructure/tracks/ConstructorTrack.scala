package klab.track.infreastructure.tracks

import klab.track.formating.{CompatibleWithJSON, ExportCSV}
import klab.track.geometry.position.Pos
import klab.track.ParticleTrack
import play.api.libs.json.{Json, JsValue}
import klab.io.formating.ExportJSON


/**
 * == A special class for importing and exporting the Track ==
 *
 * User: karolis@misiunas.com
 * Date: 19/08/2013
 * Time: 16:50
 */
trait ConstructorTrack [Self <: ConstructorTrack[Self]]
  extends MutableTrack[Self]
  with ExportCSV
  with ExportJSON
{
  this: Self =>

  /** custom implementation - save additional information */
  override def toJson: String = {
    Json.prettyPrint(
      Json.obj(
        "ParticleTrack" -> Json.obj(
          "id" -> id,
          "experiment" -> experiment,
          "time" -> time,
          "comment" -> comment,
          "version" -> version,
          "units" -> units,
          "positions" -> list.map(_.toJsonValue)
        )
      )
    )
  }

  /** only save key information: id + positions */
  def toJsonValue: JsValue = Json.obj(
    "id" -> id,
    "positions" -> list.map(_.toJsonValue)
  )

  def fromJSON(st: String): Self = { ???
//    implicit val formats = net.liftweb.json.DefaultFormats
//    val code = parse(st)
//    if((code \\ "version").extract[Int] != ParticleTrack.version)
//      throw new Exception("Warning: the ParticleTrack file is version "+(code \\ "version").extract[Int] +
//        ", while the current version is"+version)
//    return make(
//      id = (code \\ "id").extract[Int],
//      list = (code \\ "positions").extract[List[List[Double]]].map(Pos(_)),
//      units = (code \\ "units").extract[List[String]],
//      experiment = (code \\ "experiment").extract[String],
//      comment = (code \\ "comment").extract[String],
//      time = (code \\ "time").extract[Long] )
  }

  /** Gives x-y coordinates for each frame */
  def toCSV: String =
    "t" + csvSeparator + "x" + csvSeparator + "y" + "\n" + // names
    units.mkString("", csvSeparator, csvSeparator + "\n") + // units
    list.map(p => p.t + csvSeparator + p.x + csvSeparator + p.y + csvSeparator).mkString("\n") // data - might break for large tracks!

}
