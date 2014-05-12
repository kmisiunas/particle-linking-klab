package klab.track.corrections

import klab.track.assemblies.TrackAssembly
import klab.track.Track

/**
 * == Function collection for creating track assemblies form different initial objects ==
 *
 * User: kmisiunas
 * Date: 27/11/2013
 */
object Assemble {

  def fromList(list: Iterable[Track]): TrackAssembly = TrackAssembly(list)

  /** Makes new Assembly from other assemblies that have overlapping time stamps
    *
    * @param list to construct tracks form, order determines time stamp stitching
    * @param connect if to attempt connection of the tracks
    */
  def fromOverlappingAssemblies(connect: Boolean = false)(list: Seq[TrackAssembly]): TrackAssembly = {
    // The method must be memory efficient for large data set management
    // later implement size selective method
    val size = list.map(_.size).sum // number of tracks
    // check if ids overlap
    val ids: List[Int] = list.map( _.map(_.id)).flatten.toList
    if (ids.distinct.size != ids.size) throw new RuntimeException("duplicate ids are not allowed in new Assemblies")
    if(connect) ???
    list.tail.foldLeft(list.head)( (sum, el) => sum.append(el) )    // naive method - memory intense
  }




}