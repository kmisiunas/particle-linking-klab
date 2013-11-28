package klab.track.assemblies

import net.liftweb.json.JsonAST.{JDouble, JField}
import org.joda.time.DateTime
import klab.track.ParticleTrack
import scala.collection
import klab.track.geometry.position.Pos

/**
 * == Immutable Track Assembly ==
 *
 * A class for storing an managing immutable tracks. Keep the list ordered according to their key.
 *
 * Most nuts and bolts are in Assembly class
 *
 * TODO:
 *  - make toList very efficient by storing it in a list!
 *  - if stored as a Seq, make it time ordered
 *  - map not used that often, use lazy map evaluation
 *  - make an abstract class with ability to work on separate parts of the assembly ->
 *        is list or other structure better for this?
 *  - maybe not assemble should be savable but particle tracks? Thus allowing manipulation of small objects while long tracks are in memory?
 *  - Mutable state not necessary - jut make efficient non-mutable state
 *  - very efficient memory management
 *  - JSON can save LQpos via -> {t,x,y,z,NaN}
 *
 * User: karolis@misiunas.com
 * Date: 12/07/2013
 * Time: 15:50
 */
class TrackAssembly private (val listMap : Map[Int, ParticleTrack],
                             override val experiment:String,
                             override val comment: String,
                             override val time: Long)
  extends Assembly(experiment, comment, time) {


  def fromJSON(st: String): TrackAssembly = TrackAssembly.fromJSON(st)

  override def toString : String = "TrackAssembly("+size+ " tracks, "+ comment + ")"

  override lazy val size : Int = listMap.size
  def toImmutable: TrackAssembly = this
  def copy : TrackAssembly = this // immutable implementation - no need for a copy
  def toMutable: TrackAssemblyM = TrackAssemblyM(collection.mutable.Map(listMap.toSeq: _*), experiment, comment, time)
  protected def updateMap(map: Map[Int, ParticleTrack]): TrackAssembly = TrackAssembly(map, experiment, comment, time)

  /** forall a function on all ParticleTracks - expensive, try to minimise calls to it */
  def changeEach(f: (ParticleTrack) => ParticleTrack) : TrackAssembly  =
    updateMap( listMap.map( m =>  (m._1, f(m._2))).toMap )

  def remove(s: Iterable[ParticleTrack]): TrackAssembly  = {
    val set = s.map(_.id).toSet
    return TrackAssembly(listMap.filterNot(m => set(m._1)) , experiment, comment, time)
  }

  def add(list: Iterable[ParticleTrack]): TrackAssembly =
    TrackAssembly(listMap ++ (list.map(pt => (pt.id, pt)).toMap) , experiment, comment, time)

  def add(f: (List[ParticleTrack]) => List[ParticleTrack]): TrackAssembly = add(f(this))

  def apply(f: (List[ParticleTrack]) => List[ParticleTrack]): TrackAssembly =
    TrackAssembly(f( this.toList ), experiment, comment, time)

  /** Method for appending another TrackAssembly with time frames where other have left off */
  def append(list: Iterable[ParticleTrack], timeGap: Double): TrackAssembly = {
    val lastT: Double = this.maxBy(_.timeRange._2).timeRange._2
    val fdT: Pos => Pos = p => p ++ Pos(timeGap + lastT, 0,0,0)
    val shifted = list.map(_.changePositions(fdT)).toList
    this.add(shifted.head.changePositions( p => p.toLQPos) :: shifted.tail)
  }

  /** approximate size of this particle track assembly */
  lazy val memory: Double = listMap.foldLeft(0.0)( (sum:Double, el:(Int,ParticleTrack)) => sum + el._2.size  )
}

/**
 * Object for static function access
 */
object TrackAssembly {

  def apply(listMap : Map[Int, ParticleTrack], experiment: String, comment: String, time: Long) : TrackAssembly=
    new TrackAssembly(listMap, experiment,comment,time)
  def apply(list: Iterable[ParticleTrack],
            experiment: String  = "Experiment_on_"+ DateTime.now().toLocalDate.toString,
            comment:String = "" ,
            time:Long = System.currentTimeMillis()) : TrackAssembly=
    TrackAssembly(list.toSeq.sortWith(_.id < _.id).map(pt => (pt.id, pt)).toMap, experiment, comment, time)
  def apply(json: String) : TrackAssembly= fromJSON(json)

  def fromJSON(st : String) : TrackAssembly = {
    val list = Assembly.fromJSONprep(st)
    return TrackAssembly(list, list.head.experiment, list.head.comment, list.head.time)
  }

}
