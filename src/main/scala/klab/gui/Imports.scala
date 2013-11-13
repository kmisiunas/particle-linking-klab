package klab.gui

/**
 * ## Auto importer for REPL
 *
 * All the standard tools should be listed under Imports.main
 *
 * The imports should be separate if we want them to be analysed for TAB list available commands - to test this
 *
 * User: karolis@misiunas.com
 * Date: 21/07/2013
 * Time: 00:59
 */
object Imports {

  /** Main imports for REPL */
  val main: List[String] = List(
    "klab._",
    "klab.track._",             // add default imports
    "klab.track.{ParticleTrack}",             // add default imports
    "klab.track.assemblies.{TrackAssembly}",             // add default imports
    "klab.io.{Load,Save,Path,Import}" ,            // add default imports
    "klab.track.geometry.{Channel}",
    "klab.track.geometry.position.{Pos}",
    "klab.track.analysis._",
    "klab.track.analysis.{Diffusion,LocalDiffusion,Find,Proximity,Transition}",
    "klab.track.corrections._",
    "klab.track.corrections.{Filter,Continuum,Confinement}",
    "klab.gui.show.Show",
    "klab.gui.repl.Print.println",
    "sys.exit"
  )

  /** Automatic import to provided REPL */
  def auto() = ???

}