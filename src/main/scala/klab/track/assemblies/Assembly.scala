package klab.track.assemblies

import klab.track.Track
import klab.track.geometry.position.Pos
import play.api.libs.json.{Json, JsValue}
import klab.io.formating.ExportJSON


/**
 * == An Assembly of tracks - trait specialised ==
 *
 *  Specification:
 *   - units must be the same within an assembly
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 20:36
 */
abstract class Assembly (val experiment:String, val comment: String, val time: Long)
  extends Iterable[Track] with ExportJSON{

  def range: (Pos, Pos) = {
    val max = foldLeft(head.max)( (m, t) => m.max(t.max) )
    val min = foldLeft(head.min)( (m, t) => m.min(t.max) )
    (min, max)
  }

  // ---------------- Some abstract methods --------------

  /** number of tracks in this assembly */
  def size : Int
  /** the version of the class */
  def version = Assembly.version
  /** access the map where the data is stored */
  def listMap : collection.Map[Int, Track]


  // ---------------- General implemented methods --------

  /** insanely important for Assembly to perform as collection */
  def iterator: Iterator[Track] = listMap.valuesIterator

  /** access the Track with specific ID */
  def apply(id : Int) : Track = listMap(id)

  /** get multiple tracks as a list */
  def apply(first: Int, other: Int* ): List[Track] =
    apply(first) :: other.toList.map(apply(_))

  /** will give particles in specified range, non existing tracks are skipped */
  def apply(range: Range): List[Track] = range.toList.filter(contains(_)).map(apply(_))

  def contains(id: Int): Boolean = listMap.contains(id)

  def units: List[String] = this.head.units

  /** Check is assembly conforms to desired specs */
  @deprecated
  def qualityCheck : Boolean = isQuality

  def isQuality: Boolean = {
    val l = toList // safe for mutable and parallel sets
    // check for common units
    l.forall(_.units == l.head.units)
  }

  def toJsonValue: JsValue = ??? // no need for this yet!

  override def toJson: String = toJsonIterator.mkString("\n")

  /** Custom implementation for very large data sets.
    * Does not work, because ++ operator gets stack overflow.
    * Solution seems to be comming in 2.11 */
  override def toJsonIterator: Iterator[String] = {
    val woTracks = Json.prettyPrint(
      Json.obj(
        "TrackAssembly" -> Json.obj(
          "number" -> size,
          "experiment" -> experiment,
          "time" -> time,
          "comment" -> comment,
          "version" -> version,
          "array_format" -> List("time_stamp","x","y","z(optional)","param(optional)"),
          "units" -> units,
          "Track" -> "insert_PT_here"
        )
      )
    )
    // break up string and add array annotation
    val parts = woTracks.split("\"insert_PT_here\"")
    parts(0) = parts(0) + "[\n"
    parts(1) = "\n]" + parts(1)

    val lastId = listMap.keys.toList.sorted.last

    def evalJS(id: Int): String = {
      Json.prettyPrint(apply(id).toJsonValue) + (if(id != lastId) "," else "")
    }

    // not sequential! - loads entire thing into memory
    Iterator(parts(0)) ++ listMap.keys.toList.sorted.map(evalJS(_)).toIterator ++ Iterator(parts(1))

    // gives StackOverflowError as of 2.10.3
    //listMap.keys.toList.sorted
    //  .foldLeft(Iterator(parts(0)))( (sum, id) => sum ++ Iterator(evalJS(id)) ) ++ Iterator(parts(1))
  }


  /** approximate size of this particle track assembly */
  def memory: Double


  // ---------------- Manipulate methods --------------

  /** forall a function on all ParticleTracks - expensive, try to minimise calls to it */
  def changeEach(f : (Track => Track)) : Assembly

  /** Make a copy of this Assembly */
  //def copy : Assembly

  /** removes specified tracks from the assembly */
  def remove(s: Iterable[Track]): Assembly

  /** adds particles to the assembly */
  def add(s :Iterable[Track]): Assembly
  /** adds new list of tracks that was generated form current list */
  def add(f: (List[Track]) => List[Track]): Assembly

  def apply(f : List[Track] => List[Track] ) : Assembly

  /** Method for appending another TrackAssembly with time frames where other have left off */
  def append(list: Iterable[Track], timeGap: Double = 0.0, follow: Boolean = false): Assembly

  // ------------------ Id management ---------------

  class IdMaker(private var lastId: Int){
    def next(): Int = {
      lastId = lastId + 1
      lastId
    }
  }

  /** returns id maker that can make increasing numbers of id's */
  def getIdMaker: IdMaker = new IdMaker( listMap.keys.max )

}

object Assembly {

  /** an important implicit conversion  - many method rely on this to function*/
  implicit def assemblyToList(ta: Assembly) = ta.toList

  val version = 2

}