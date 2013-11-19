package klab.gui.infrastructure.show

import klab.track.ParticleTrack
import klab.gui.Print

/**
 * ## Method for organising a display of a particle track
 *
 * User: kmisiunas
 * Date: 18/11/2013
 */
object ShowParticleTrack extends ShowType {

  /** Returns true if this handler can deal with this data type */
  def isType(list: Seq[AnyRef]): Boolean = list.head match {
    case x:ParticleTrack => true
    case (x:ParticleTrack) :: _ => true
    case _ => false
  }

  /** Returns iterator that will be written to a file - a line for each string */
  def show(list: Seq[AnyRef]): Unit = list.head match {
    case pt: ParticleTrack => list.size match {
      case 1 => klab.gui.windows.ShowParticleTrack.show( pt )
      case _ => klab.gui.windows.ShowParticleTrack.show( list.toList.map(_.asInstanceOf[ParticleTrack]) )
    }
    case l: Iterable[ParticleTrack] => klab.gui.windows.ShowParticleTrack.show(l)
    case _ => Print.error("Error: this type could not be shown after recognition")
  }
}
