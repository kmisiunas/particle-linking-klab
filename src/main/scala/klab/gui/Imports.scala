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
    "klab.track.{Track}",             // add default imports
    "klab.track.assemblies.{TrackAssembly}",             // add default imports
    "klab.io.{Load,Save,Path,Import,MatLab,Mathematica}" ,            // add default imports
    "klab.track.geometry.{Channel}",
    "klab.track.geometry.position.{Pos}",
    "klab.track.analysis._",
    "klab.track.analysis.{Diffusion,DiffusionLocal,Find,Analyse,DiffusionCorrelation}",
    "klab.track.corrections._",
    "klab.track.corrections.{Filter,Correction,Assemble}",
    "com.misiunas.geoscala.vectors.Vec",
    "com.misiunas.geoscala.Point",
    "klab.track.builders.{LinkTracks,BuildTracks}",
    "klab.track.operators.TransformTrack"
  ) ::: breeze ::: user ::: operators

  /** imports of Breeze libraries for REPL */
  val breeze: List[String] = List(
    "breeze.linalg.{DenseVector,DenseMatrix}"
  )

  val user: List[String] = List(
    "klab.gui.{Show,Print,TimeIt}",
    "klab.gui.Print.println"
  )

  val operators: List[String] = List(
    "klab.track.operators.OneTrack",
    "klab.track.operators.TwoTracks",
    "klab.track.operators.TimeOperator"
  )



  /** Automatic import to provided REPL */
  def auto() = ???

}