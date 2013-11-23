package klab.track.assemblies

import klab.track.ParticleTrack
import org.joda.time.DateTime
import collection.mutable.Map
import scala.collection
import scala.collection

/**
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 20:36
 */
class TrackAssemblyM private (val listMap : Map[Int, ParticleTrack],
                              override val experiment:String,
                              override val comment: String,
                              override val time: Long)
  extends Assembly(experiment, comment, time) {


  def fromJSON(st: String): TrackAssembly = TrackAssembly.fromJSON(st)

  override def toString : String = "TrackAssemblyM("+size+ " tracks, "+ comment + ")"

  override def size : Int = listMap.size
  def toImmutable: TrackAssembly = TrackAssembly( collection.immutable.Map(listMap.toSeq: _*), experiment, comment, time)
  /** Makes a copy of mutable list - be careful with memory usage */
  def copy : TrackAssemblyM = TrackAssemblyM(listMap.clone(), experiment, comment, time)
  def toMutable: TrackAssemblyM = this

  /** Specialised copy that takes the Map to be updated Make a copy of this Assembly */
  protected def updateMap(map: Map[Int, ParticleTrack]): TrackAssemblyM = TrackAssemblyM(map, experiment, comment, time)

  /** forall a function on all ParticleTracks - expensive, try to minimise calls to it */
  def changeEach(f: (ParticleTrack) => ParticleTrack): TrackAssemblyM = {
    listMap.foreach( m => listMap.update(m._1, f(m._2)) )
    return this
  }

  def remove(s: Iterable[ParticleTrack]): TrackAssemblyM = {
    s.map(_.id).foreach(listMap.remove(_))
    return this
  }

  def add(s: Iterable[ParticleTrack]): TrackAssemblyM = {
    listMap ++= (s.map(pt => (pt.id, pt)).toMap)
    return this
  }

  def apply(f: (List[ParticleTrack]) => List[ParticleTrack]): Assembly = ???

  /** adds new list of tracks that was generated form current list */
  def add(f: (List[ParticleTrack]) => List[ParticleTrack]): Assembly = ???

  /** Method for appending another TrackAssembly with time frames where other have left off */
  def append(list: Iterable[ParticleTrack], timeGap: Double): Assembly = ???

  /** approximate size of this particle track assembly */
  def memory: Double = ???
}

/**
 * Object for static function access
 */
object TrackAssemblyM {

  def apply(listMap : Map[Int, ParticleTrack],
            experiment: String = "Experiment_on_"+ DateTime.now().toLocalDate.toString,
            comment: String = "",
            time: Long = System.currentTimeMillis()) : TrackAssemblyM=
    new TrackAssemblyM(listMap, experiment,comment,time)
  def apply(list: Seq[ParticleTrack], experiment: String, comment:String, time:Long) : TrackAssemblyM =
    TrackAssemblyM(Map(list.sortWith(_.id < _.id).map(pt => (pt.id, pt)).toMap.toSeq: _*), experiment, comment, time)
  def apply(json: String) = fromJSON(json)

  def fromJSON(st : String) : TrackAssemblyM = {
    val list = Assembly.fromJSONprep(st)
    return TrackAssemblyM(list, list.head.experiment, list.head.comment, list.head.time)
  }

}
