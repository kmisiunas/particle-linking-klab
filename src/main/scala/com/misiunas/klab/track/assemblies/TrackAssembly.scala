package com.misiunas.klab.track.assemblies

import net.liftweb.json.JsonAST.{JDouble, JField}
import org.joda.time.DateTime
import com.misiunas.klab.track.ParticleTrack
import scala.collection
import scala.collection

/**
 * == Immutable Track Assembly ==
 *
 * A class for storing an managing immutable tracks.
 *
 * Most nuts and bolts are in Assembly class
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
  def changeEach(f: (ParticleTrack) => ParticleTrack): Assembly =
    updateMap( listMap.map( m =>  (m._1, f(m._2))).toMap )

  def remove(s: Seq[ParticleTrack]): TrackAssembly = {
    val set = s.map(_.id).toSet
    return TrackAssembly(listMap.filterNot(m => set(m._1)) , experiment, comment, time)
  }

  def add(s: Seq[ParticleTrack]): TrackAssembly =
    TrackAssembly(listMap ++ (s.map(pt => (pt.id, pt)).toMap) , experiment, comment, time)
}

/**
 * Object for static function access
 */
object TrackAssembly {

  def apply(listMap : Map[Int, ParticleTrack],
            experiment: String = "Experiment_on_"+ DateTime.now().toLocalDate.toString,
            comment: String = "",
            time: Long = System.currentTimeMillis()) : TrackAssembly=
    new TrackAssembly(listMap, experiment,comment,time)
  def apply(list: Seq[ParticleTrack], experiment: String, comment:String, time:Long) : TrackAssembly=
    TrackAssembly(list.sortWith(_.id < _.id).map(pt => (pt.id, pt)).toMap, experiment, comment, time)
  def apply(json: String) : TrackAssembly= fromJSON(json)

  def fromJSON(st : String) : TrackAssembly = {
    val list = Assembly.fromJSONprep(st)
    return TrackAssembly(list, list.head.experiment, list.head.comment, list.head.time)
  }

}
