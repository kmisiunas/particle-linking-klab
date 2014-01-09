package klab.io.infrastructure.load

import klab.track.assemblies.TrackAssembly
import scala.collection.mutable.ListBuffer
import klab.track.ParticleTrack
import klab.io.Path

/**
 * User: karolis@misiunas.com
 * Date: 20/08/2013
 * Time: 17:06
 */
object ImportTrackAssembly {

  val trackExtension = "txt"

  val filterTitlesWith = List("filter", "frame", "length", "comment", "info", "parameter", "overlap",
    "info", "description", "json")

  /** scans provided dir and creates track assembly out of it */
  def fromTable(dir: String, experiment: String): TrackAssembly = {
    val build: ListBuffer[ParticleTrack] = ListBuffer()
    /** recursive function to add files in the dir */
    def addFilesInDir(inDir: Path): Unit = {
      inDir.listFiles
        .filter(_.extension == trackExtension) // extension must be correct
        .filter( f => filterTitlesWith.forall( !f.name.contains(_) ) )
        .map(f => build.append( ImportParticleTrack.fromTable(f, experiment, comment = f.diff(Path(dir))) ))
      inDir.listDirs.foreach( addFilesInDir(_) ) // explore subdirectories
    }
    addFilesInDir(Path(dir))
    TrackAssembly(build.toList, experiment, "assembly from dir: " + dir)
  }
}
