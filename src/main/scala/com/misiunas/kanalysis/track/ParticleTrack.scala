package com.misiunas.kanalysis.track

import org.joda.time.DateTime
import scala.util.parsing.json.JSONObject

/**
 * ==Particle Track==
 *
 * -------------------------------------------------------
 *
 * A special object for storing particle track.
 * It should contain: id, {x,y,z} positions, time stamp (ns)
 * also any additional information about the track.
 * Object should be backward compatible or indicate which
 * version it is using. Also functional manipulations should be implemented.
 *
 * Versions:
 *  - v1 - initial release (scala 2.10)
 *
 *  @author karolis@misiunas.com,
 *  Date: 11/07/2013,
 *  Time: 14:28
 */
class ParticleTrack(id: Int, // ID of the particle
                    experiment:String = "Experiment_on_"+ DateTime.now().toLocalDate.toString, // the experiment title
                    time:Long = System.currentTimeMillis()) extends Serializable {

  /** version of particle track object */
  val version = 1

  /** x,y,z coordinate of the particle */
  type Coordinate = Array[Double]

  /** values are stored here */
  var list: List[Coordinate] = Nil


}