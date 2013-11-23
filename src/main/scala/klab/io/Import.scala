package klab.io

import klab.track.assemblies.TrackAssembly
import klab.io.infrastructure.load.ImportTrackAssembly

/**
 * == Import objects into KLab ==
 *
 * User: karolis@misiunas.com
 * Date: 20/08/2013
 * Time: 15:38
 */
object Import {

  def apply(kind: String, path: String): Any = ???

  /** Imports multiple track files in specified dir and makes a track assembly
    * @param dir path to directory where the single track files are
    * @param experiment - describe the experiment
    */
  def dirToAssembly(dir: String, experiment: String = ""): TrackAssembly =
    ImportTrackAssembly.fromTable(dir, experiment)


  /** Imports TrackAssembly from a JSON file */
  def jsonToAssembly(file: String = Path.find()): TrackAssembly = TrackAssembly(Load(file))


}
