package com.misiunas.klab

import com.misiunas.klab.track.position.Pos

/**
 * == Track klab ==
 *
 * This klab defines the basic principles for representing particle motion.
 *
 * User: karolis@misiunas.com
 * Date: 17/07/2013
 * Time: 15:52
 */
package object track {

  /** represent initial and final times of something (track?) */
  type TimeRange = (Double, Double)

  /** returns space-time range that bounds the particle motion */
  type STRange = (Pos, Pos)

  /** Particle track can always be found by a unique id */
  type ID = Int

}
