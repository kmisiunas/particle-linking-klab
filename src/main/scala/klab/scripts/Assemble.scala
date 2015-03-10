package klab.scripts

import klab.KLab.AppConfig
import klab.gui.Print
import klab.gui.Print._
import klab.io.{Import, Save, Path}
import klab.track.assemblies.{TrackAssembly, Assembly}
import klab.track.geometry.position.Pos
import org.joda.time.DateTime
import scopt.OptionParser

/**
 * # Script: assembles multiple track assemblies into one
 *
 * Created by kmisiunas on 15-03-03.
 */
class Assemble extends ScriptTemplate {
  import Print.{println, debug} // default output

  /** list of names that allows to invoke this script with */
  override def name: String = "assemble"

  /** description and configuration options */
  override def config(op: OptionParser[AppConfig]): Unit = {
    import op._
    import Assemble.Config._
    cmd( name ) action { (_, c) =>
      c.copy(mode = "script:"+name) } text(
      """takes multiple track assemblies and combines them into one.
        |Input: /tracks/*.json in trajectory assembly format
        |Output: /assemblies/combined_n(number)_(date).json the net assembly
        |If files are supplied it puts them in the same folder.
      """.stripMargin) children(
      opt[Int]("spacing") action { (x, c) => {spacing = x; c} }
        text("add time spacer between joined assembles (default:"+spacing+")"),
      opt[Unit]("follow") action { (_, c) => {linkTracks = true; c} }
        text("time component never overlaps between assemblies and is allined to follow previous assembly"),
      opt[Unit]("link") action { (_, c) => {linkTracks = true; c} }
        text("(todo) attempt to link up tracks between different assemblies"),
      checkConfig { c => if (spacing < 0) failure("spacing between tracks must be >= 0") else success }
      )
    note("")
  }

  /** runs the script */
  override def run(options: AppConfig): Unit = {
    Print.globalDebug = options.debug
    Print.globalSilent = options.toScreen
    debug("passed files:\n" + options.files.mkString("\n"))
    // get input files
    val in = options.files.size match {
      case 0 => structuredDirectorySweep()
      case 1 =>
        {Print.error("need to supply at least two files to make a joined assembly");
          throw new IllegalArgumentException("need to supply at least two files to make a joined assembly")}
      case x if x>1 => options.files
      case _ => throw new IllegalArgumentException("script: unknown number of files supplied")
    }
    // combine
    val combined = combine( in )
    // produce output
    options.toScreen match {
      case true => System.out.print( combined.toJson )
      case false if options.files.size == 0 =>
        Save( combined, (Path.current / "assemblies/" / generateOutputName(in.size)) )
      case false =>
        Save( combined, (in.head.dir / generateOutputName(in.size)) )
    }

  }

  /** generate output file name */
  def generateOutputName(n: Int): String = "combined_n"+n+"_"+ DateTime.now.toString("YYYY-MM-DD")+".json"

  /** helper: sweeps structured directory */
  def structuredDirectorySweep(): Seq[Path] = {
    // check structure
    if ((Path.current / "tracks/").exists == false)
      throw new IllegalArgumentException("directory /tracks/ was not found. Please create it and put some trajectories files (*.json) in")
    // get files
    val in = (Path.current / "tracks/").listFiles.filter(_.extension == "json").sortBy(_.fileName)
    if (in.isEmpty) throw new IllegalArgumentException("no input JSON files found in  /tracks/ dir")
    println("Input: found "+in.size+" JSON files in /tracks/ dir")
    // perform combination
    in
  }

  // ############# KEY SCRIPT ###############
  /** combines tracks */
  def combine(files: Seq[Path]): Assembly = {
    import Assemble._
    debug("Attempting to combine the assemblies into one")
    val raw = files.map( file => Import.jsonToAssembly(file) )
    raw.reduceLeft( (sum, el) => sum.append( el, timeGap = Config.spacing, follow = Config.follow) )
  }

}


private object Assemble {

  object Config {
    // add extra time spacing between tracks
    var spacing: Int = 0
    // attempt at linking up tracks
    var linkTracks: Boolean = false
    // make tracks follow each other in time components
    var follow: Boolean = false
  }

}
