package klab.gui

/**
 * User: karolis@misiunas.com
 * Date: 21/07/2013
 * Time: 00:59
 */
object Imports {

  val main : List[String] = List(
    "klab._",
    "klab.track._",             // add default imports
    "klab.track.assemblies._",             // add default imports
    "klab.io.{Load,Save,Path,Import}" ,            // add default imports
    "klab.track.geometry._",
    "klab.track.analysis._",
    "klab.track.corrections._",
    "klab.gui.show.Show",
    "klab.gui.repl.Print.println",
    "sys.exit"
  )

}