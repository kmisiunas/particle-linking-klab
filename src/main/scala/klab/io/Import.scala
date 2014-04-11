package klab.io

import klab.track.assemblies.TrackAssembly
import klab.io.infrastructure.load.{ImportCSVRawTracking, ImportTrackAssembly}
import klab.track.geometry.position.Pos
import klab.io.infrastructure.load.stefanos.ImportStefanosRawTracking

/**
 * == Import objects into KLab ==
 *
 * User: karolis@misiunas.com
 * Date: 20/08/2013
 * Time: 15:38
 */
object Import {

  /** Imports multiple track files in specified dir and makes a track assembly
    * @param dir path to directory where the single track files are
    * @param experiment - describe the experiment
    */
  def dirToAssembly(dir: String = Path.find(), experiment: String = "unnamed"): TrackAssembly =
    ImportTrackAssembly.fromTable(dir, experiment)


  /** Imports TrackAssembly from a JSON file */
  def jsonToAssembly(file: String = Path.find()): TrackAssembly = TrackAssembly(Load(file))


  /** Import a dir with files from raw Stefanos input */
  def dirToListPos(dir: String = Path.find()): List[Pos] = ImportStefanosRawTracking.apply(Path(dir))

  /** Import a raw csv file from video tracker */
  def csvToListPos(dir: String = Path.find()): List[Pos] = ImportCSVRawTracking.apply(Path(dir))

}
