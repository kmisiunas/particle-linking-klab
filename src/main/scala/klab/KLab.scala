package klab

import java.io.File
import javax.swing.{JFrame, JOptionPane, UIManager}
import klab.gui.repl.Terminal
import klab.io.Path
import java.awt.datatransfer.{Clipboard, StringSelection}
import java.awt.{Image, Component, Toolkit}
import com.apple.eawt.Application
import klab.scripts.ScriptTemplate

/**
 * Main class
 * ----------
 *
 * Entry point for KLab software.
 *
 * Created with IntelliJ IDEA.
 * User: kmisiunas
 * Date: 10/11/2013
 * Time: 00:39
 */
object KLab {

  val appName = "KLab"

  val appVersion = "0.3.0"

  lazy val guiOwner: Component = {
    val frame = new JFrame(appName)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.getContentPane
  }

  case class AppConfig(mode: String = "repl",
                    toScreen: Boolean = false,
                    debug: Boolean = false,
                    files: Seq[Path] = Seq()  )


  /** Main object */
  def main(args: Array[String]) {

    val parser = new scopt.OptionParser[AppConfig](appName.toLowerCase) {
      head(appName, appVersion)

      note(
        """Program allows to analyse and reconstruct particle trajectories.
          |It can run in dynamic and script mode. Dynamic mode allows
          |direct input in scala language - just run "klab", while scripting
          |mode runs predefined scripts for quick processing.
          |
          |Most scripts are designed to work with structured folders, where:
          |/raw_points/*.csv - contains raw {t,x,y,..} points
          |/tracks/*.json - contains corresponding trajectories
          |/assemblies/*.json - have collected trajectories
          |""".stripMargin)

      help("help") hidden() //text("prints this usage text")
      version("version") hidden()

      // todo
      opt[Unit]("to-screen") action { (_, c) =>
        c.copy(toScreen = true) } text("prints results to the screen and suppresses other outputs")

      opt[Unit]("debug") action { (_, c) =>
        c.copy(debug = true) } text("print debugging information to the screen")

      arg[File]("<file>...") unbounded() optional() action { (x, c) =>
        c.copy(files = c.files :+  Path(x) ) } text("files to perform script analysis on")

      note("\nAvailable scripts:\n")

      // include all scripts
      ScriptTemplate.list.foreach( _.config(this) )

//      cmd("assemble") action { (_, c) =>
//        c.copy(mode = "script:assemble") } text("combines multiple tracks into one large set") children(
//          opt[Int]("spacing") action { (_, c) => c } text("filter out tracks smaller than this (default:xx)"),
//          opt[Unit]("one-after-another") action { (_, c) => c } text("Assume particles can appear easily at the inlets (channel along x)")
//          //checkConfig { c => if (c.spacing < 0) failure("spacing between tracks must be >= 0") else success }
//        )
//
//      cmd("info") action { (_, c) =>
//        c.copy(mode = "script:info") } text("display available general info about the tracks or folder")

    }


    // Parse and execute the program
    parser.parse(args, AppConfig()) match {
      case Some(config) if config.mode == "repl" =>
      {
        if (util.Properties.isMac) runTerminalWithDecorations()
        else Terminal()
      }
      case Some(config) if config.mode.contains("script") =>
      {
        val mode = config.mode.drop(7).trim.toLowerCase
        ScriptTemplate.list.find(_.name == mode) match {
          case Some(script) => script.run(config)
          case None => println("KLab: script failed to execute: "+config.mode)
        }
      }
      case Some(config) =>
        println("KLab: unknown command: "+config.mode)
      case None => // arguments are bad, error message will have been displayed
        println("KLab error: could not understand supplied arguments")
    }


    // Custom terminal for Mac OS X
    def runTerminalWithDecorations(): Unit = {
      // Get image
      val application: Application = Application.getApplication()
      val image: Image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png"))
      application.setDockIconImage(image)
      // change name - does not work!
      System.setProperty("apple.laf.useScreenMenuBar", "true")
      System.setProperty("com.apple.mrj.application.apple.menu.about.name", "KLab in Background")
      UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() )
      // Start terminal
      Terminal()
    }

  }

}
