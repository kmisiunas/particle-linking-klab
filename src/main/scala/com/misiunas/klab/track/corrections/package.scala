package com.misiunas.klab.track

/**
 * == Package Contains objects to modify the assemblies containing particle tracks ==
 *
 * User: karolis@misiunas.com
 * Date: 05/08/2013
 * Time: 15:42
 */
package object corrections {

  /** This type is supported by Assembly object. Just use apply( f: PTFilter ) */
  type PTFilter = Iterable[ParticleTrack] => List[ParticleTrack]

}
