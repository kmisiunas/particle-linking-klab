package com.misiunas.klab.track.assemblies

import com.misiunas.klab.track.formating.CompatibleWithJSON
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import com.misiunas.klab.track.ParticleTrack
import com.misiunas.klab.track.geometry.position.Pos


/**
 * == An Assembly of tracks - trait infrastructure ==
 *
 *
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 20:36
 */
abstract class Assembly (val experiment:String, val comment: String, val time: Long)
  extends Iterable[ParticleTrack] with CompatibleWithJSON[Assembly]{

  // ---------------- Some abstract methods --------------

  /** number of tracks in this assembly */
  def size : Int
  /** the version of the class */
  def version = Assembly.version
  /** convert to mutable */
  def toMutable : TrackAssemblyM
  /** convert to immutable */
  def toImmutable : TrackAssembly
  /** access the map where the data is stored */
  def listMap : collection.Map[Int, ParticleTrack]


  // ---------------- General implemented methods --------

  /** insanely important for Assembly to perform as collection */
  def iterator: Iterator[ParticleTrack] = listMap.valuesIterator

  /** access the ParticleTrack with specific ID */
  def apply(id : Int) : ParticleTrack = listMap(id)

  /** get multiple tracks as a list */
  def apply(first: Int, other: Int* ): List[ParticleTrack] =
    apply(first) :: other.toList.map(apply(_))

  /** will give particles in specified range, non existing tracks are skipped */
  def apply(range: Range): List[ParticleTrack] = range.toList.filter(contains(_)).map(apply(_))

  def contains(id: Int): Boolean = listMap.contains(id)

  /** Check is assembly conforms to desired specs */
  def qualityCheck : Boolean = {
    val l = toList // safe for mutable and parallel sets
    // check for common units
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
        ("units" -> this.head.units) ~
        ("ParticleTrack" ->
          this.map { pt =>
            (("id" -> pt.id) ~
              ("positions" -> pt.positions.map(_.list)))
          }))

  def toJSON : String = pretty(render(json))


  // ---------------- Manipulate methods --------------

  /** forall a function on all ParticleTracks - expensive, try to minimise calls to it */
  def changeEach(f : (ParticleTrack => ParticleTrack)) : Assembly

  /** Make a copy of this Assembly */
  def copy : Assembly

  /** removes specified tracks from the assembly */
  def remove(s: Seq[ParticleTrack]): Assembly

  /** adds particles to the assembly */
  def add(s :Seq[ParticleTrack]): Assembly
  /** adds new list of tracks that was generated form current list */
  def add(f: (List[ParticleTrack]) => List[ParticleTrack]): Assembly

  def apply(f : List[ParticleTrack] => List[ParticleTrack] ) : Assembly
}

object Assembly {

  /** an important implicit conversion  - many method rely on this to function*/
  implicit def assemblyToList(ta: Assembly) = ta.toList

  val version = 2

  /** general method for turning the JSON file into data - preparation as there is no constructors yet */
  def fromJSONprep(st: String) : List[ParticleTrack] = {
    implicit val formats = net.liftweb.json.DefaultFormats
    val code = parse(st)
    if((code \\ "version").extract[Int] > Assembly.version) throw new Exception("Warning: the ParticleTrack file is version "+(code \\ "version").extract[Int] + ", while the current version is"+version)
    val experiment = (code \\ "experiment").extract[String]
    val comment = (code \\ "comment").extract[String]
    val time = (code \\ "time").extract[Long]
    val units = (code \\ "units").extract[List[String]]
    (code \\ "ParticleTrack").extract[List[JObject]].map( sub => ParticleTrack(
      id = (sub \\ "id").extract[Int],
      list = (sub \\ "positions").extract[List[List[Double]]].map(Pos(_)),
      units, experiment, comment, time ) )
  }

}