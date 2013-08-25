package com.misiunas.klab.track

/**
 * == Package for analysing the particle track data ==
 *
 *
 *
 * User: karolis@misiunas.com
 * Date: 05/08/2013
 * Time: 15:38
 */
package object analysis {

  type PTFind = Iterable[ParticleTrack] => Set[ParticleTrack]
}
