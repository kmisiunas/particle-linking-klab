package klab.scripts

import klab.KLab.{AppConfig}
import klab.gui.Print
import klab.io.{Import, Save, Path}
import klab.track.assemblies.{TrackAssembly, Assembly}
import klab.track.geometry.position.Pos
import scopt.OptionParser

/**
 * # Script: automatically initiates tracking routine
 *
 * Not a functional design - static parameters
 *
 * Created by kmisiunas on 15-03-01.
 */
class Track extends ScriptTemplate {
  import Print.{println, debug} // default output

  /** list of names that allows to invoke this script with */
  override def name: String = "track"

  /** description and configuration options */
  override def config(op: OptionParser[AppConfig]): Unit = {
    import op._
    cmd( name ) action { (_, c) =>
      c.copy(mode = "script:"+name) } text(
        """reconstructs particle trajectories from points.
          |Input: /raw_points/*.csv in format of time, x, y, z, additional parameters
          |Output: /tracks/*.json  corresponding trajectories
          |If files are supplied it puts them in /*.json or prints on screen.
        """.stripMargin) children(
        opt[String]("method") optional() action { (x, c) => {Track.Config.method = x; c} }
          text("todo: tracking method (default:"+Track.Config.method+")"),
        opt[Double]("max") action { (x, c) => {Track.Config.maxDisplacement = x; c} }
          text("max displacement distance (default:"+Track.Config.maxDisplacement +")"),
        opt[Int]("maxT") action { (x, c) => {Track.Config.maxT = x; c} }
          text("max difference between frames (default:"+Track.Config.maxT+")"),
        opt[Int]("filter") action { (x, c) => {Track.Config.filter = x; c} }
          text("filter out tracks smaller than this (default:"+Track.Config.filter+")"),
        opt[Unit]("channel") action { (_, c) => {Track.Config.channel = true; c} }
          text("todo: assume particles can appear easily at the inlets (channel along x)")
      )
    note("")
  }

  /** runs the script */
  override def run(options: AppConfig): Unit = {
    Print.globalDebug = options.debug
    Print.globalSilent = options.toScreen
    debug("passed files:\n" + options.files.mkString("\n"))
    options.files.size match {
      case 0 => structuredDirectorySweep()
      case 1 if options.toScreen => // print results to the screen
        System.out.println( trackingScript(options.files.head).toJson )
      case x if x>0 => // multiple files to /*.json
        options.files
          .map( p => (p, p.dir / (p.fileName + ".json")) )
          .foreach( t => processSingleFile(t._1, t._2) )
      case _ => Print.error("script: unknown number of files supplied")
    }
  }

  /** helper: opens, analyses, saves to output file  */
  def processSingleFile(in: Path, out: Path): Unit = Save( trackingScript(in), debug(out, "output file:"))

  /** helper: sweeps structured directory */
  def structuredDirectorySweep(): Unit = {
    // check structure
    if ((Path.current / "raw_points/").exists == false)
      {Print.error("directory /raw_points/ was not found. Please create it and put some CSV's files for me to eat"); return Unit }
    // get files
    val in = (Path.current / "raw_points/").listFiles.filter(_.extension == "csv").sortBy(_.fileName)
    if (in.isEmpty) {Print.error("no input CSV files found in  /raw_points/ dir"); return Unit }
    println("Input: found "+in.size+" CSV files in /raw_points/ dir")
    // generate output files in /tracks/*.json
    val out = in.map( f => Path.current / "tracks/" / (f.fileName + ".json") )
    // do the computation file by file
    // todo palatalization might be possible here
    in.zip(out).foreach( t => processSingleFile(t._1, t._2) )
  }


  // ############# KEY SCRIPT ###############
  /** function that performs the track reconstruction*/
  def trackingScript(file: Path): Assembly = {
    import Track.Config._
    println("Tracking: " + Path(file).name)
    val posList = Import.csvToListPos(file) // check if input is correctly formatted
    val maxSeparation = Pos(maxT, maxDisplacement, maxDisplacement, maxDisplacement)
    // todo: upgrade safe distance to automatic or a parameter.
    val tracks = klab.track.builders.BuildTracks.fromPos( maxSeparation, safeDistance = 4)(posList)
    val filtered = tracks.filter(_.size > filter)
    println("Filter tracks smaller than "+ filter +" frames. Final track count: "+filtered.size)
    TrackAssembly(filtered, experiment = "from: "+file)
  }

}


protected object Track {

  object Config {
    // filter tracks smaller than
    var filter: Int = 10
    // method for linking the tracks
    var method: String = "hungarian"
    var maxDisplacement: Double = 15.0
    var maxT: Int = 4
    // assume that it is easy to escape at the ends (on x)
    var channel = false
  }

}
