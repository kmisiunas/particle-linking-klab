package klab.track

import klab.track.assemblies.{TrackAssemblyM, TrackAssembly}

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

  //type TrackSeq <: Iterable[ParticleTrack]

  /** function for returning the same type as input.
    *
    * Prime use to is for maintenance */
  def returnSameType[A <: Iterable[ParticleTrack]](input: A)(output:Iterable[ParticleTrack]): A = {
    input match {
      case x:TrackAssembly => {
        TrackAssembly(output, experiment = x.experiment, comment = x.comment, time = x.time)
          .asInstanceOf[A]
      }
      case x:TrackAssemblyM => ???
      case x:List[ParticleTrack] => output.toList.asInstanceOf[A]
      case x:Seq[ParticleTrack] => output.toSeq.asInstanceOf[A]
      case x:Iterable[ParticleTrack] => output.toIterable.asInstanceOf[A]
      case x:Vector[ParticleTrack] => output.toVector.asInstanceOf[A]
      case _ => throw new RuntimeException("returnSameType does not support type " + input.getClass)
    }
  }
}
