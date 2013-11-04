package klab.track.infreastructure.tracks

import klab.track.formating.{CompatibleWithJSON, ExportCSV}
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import klab.track.geometry.position.Pos
import klab.track.ParticleTrack


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
  with CompatibleWithJSON[Self]
{
  this: Self =>


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

  def fromJSON(st: String): Self = {
    implicit val formats = net.liftweb.json.DefaultFormats
    val code = parse(st)
    if((code \\ "version").extract[Int] != ParticleTrack.version)
      throw new Exception("Warning: the ParticleTrack file is version "+(code \\ "version").extract[Int] +
        ", while the current version is"+version)
    return make(
      id = (code \\ "id").extract[Int],
      list = (code \\ "positions").extract[List[List[Double]]].map(Pos(_)),
      units = (code \\ "units").extract[List[String]],
      experiment = (code \\ "experiment").extract[String],
      comment = (code \\ "comment").extract[String],
      time = (code \\ "time").extract[Long] )
  }

  /** Gives x-y coordinates for each frame */
  def toCSV: String =
    "t" + csvSeparator + "x" + csvSeparator + "y" + "\n" + // names
    units.mkString("", csvSeparator, csvSeparator + "\n") + // units
    list.map(p => p.t + csvSeparator + p.x + csvSeparator + p.y + csvSeparator).mkString("\n") // data - might break for large tracks!

}
