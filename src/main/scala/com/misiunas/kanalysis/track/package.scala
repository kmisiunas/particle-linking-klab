package com.misiunas.kanalysis

import com.misiunas.kanalysis.track.position.Pos

/**
 * == Track package ==
 *
 * This package defines the basic principles for representing particle motion.
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

}
