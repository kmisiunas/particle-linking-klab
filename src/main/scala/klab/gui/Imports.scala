package klab.gui

/**
 * ## Auto importer for REPL
 *
 * All the standard tools should be listed under Imports.main
 *
 * The imports should be separate if we want them to be analysed for TAB list available commands
 *
 * User: karolis@misiunas.com
 * Date: 21/07/2013
 * Time: 00:59
 */
object Imports {

  /** Main imports for REPL */
  def main: List[String] = List(
    "klab._",
    "klab.track._",             // add default imports
    "klab.track.{ParticleTrack}",             // add default imports
    "klab.track.assemblies.{TrackAssembly}",             // add default imports
    "klab.io.{Load,Save,Path,Import}" ,            // add default imports
    "klab.track.geometry.{Channel}",
    "klab.track.geometry.position.{Pos}",
    "klab.track.analysis._",
    "klab.track.analysis.{Diffusion,DiffusionLocal,Find,Proximity,Transition,DiffusionCorrelation}",
    "klab.track.corrections._",
    "klab.track.corrections.{Filter,Continuum,Confinement}",
    "klab.gui.{Show,Print}",
    "klab.gui.Print.println",
    "com.misiunas.geoscala.vectors.Vec",
    "com.misiunas.geoscala.Point",
    "sys.exit"
  ) ::: breeze

  /** imports of Breeze libraries for REPL */
  val breeze: List[String] = List(
    "breeze.linalg.{DenseVector,DenseMatrix}",
    "breeze.plot.Figure"
  )



  /** Automatic import to provided REPL */
  def auto() = ???

}