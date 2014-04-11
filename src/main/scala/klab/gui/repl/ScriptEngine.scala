package klab.gui.repl

import klab.gui.repl.Terminal
import klab.io.Load
import scala.io.Source
import java.io.File
import java.util.zip.{ZipEntry, ZipInputStream}
import scala.collection.mutable
import klab.gui.{Print, Imports}
import klab.gui.Print.println


/**
 * == Run automated scripts within the terminal ==
 *
 * User: karolis@misiunas.com
 * Date: 30/07/2013
 * Time: 15:57
 */
class ScriptEngine (private val t: Terminal){

  /** Runs a script: First tries to locate it.  */
  def run( script : String ) = {
    def process(s: String) =  execute(ScriptEngine.prepare(s))
    // if empty or "auto" do file open
    // if not a full path, do:
    // first check dir app/scripts/
    // then check packaged scripts in .scripts
    if(script.toLowerCase == "file" || script.toLowerCase == "open") {
      println("Please select the script file (*.sc)")
      process(Load())
    }
    else if ((new File(script)).exists()) process(Load(script))
    else if ((new File("/scripts" +script+".sc")).exists()) process(Load("/scripts" +script+".sc"))
    else {
      val buildInScripts = ScriptEngine.getJarScripts()
      val sc = buildInScripts.find( _.contains("scripts/" +script+".sc") )
      if (!sc.isEmpty) {
        val raw = Source.fromInputStream(
          getClass.getResourceAsStream("/"+sc.get)
        ).getLines.mkString("\n")
        execute(ScriptEngine.prepare(raw))
      } else {
        print("To run a script type its name. New scripts should be placed in the \"scripts/\" folder. ")
        println("To run script in different directory type Run(\"file\")")
        println("Available scripts:")
        buildInScripts.foreach(f => println(" - "+ """(?<=scripts/).+(?=\.sc)""".r.findFirstIn(f).get ))
        //todo print file scrips
      }
    }
  }

  /** Sends commands for execution to ILoop REPL.
    * Should stop if there was a unhandled exception encountered.
    */
  private def execute(list: List[String]): Unit = {
    if (list.isEmpty) return ()
    val cmd = list.head
    if (cmd.startsWith("import ")) t.beSilentDuring( t.command(cmd) ) // silent imports
    else commandSc(cmd)
    if (KLabReporter.errorOccurred) {   // ups.. did not go according to plan
      Print.error("unexpected behaviour that terminated the script")
      return ()
    }
    else return execute( list.tail )
  }

  /** special printing function that shows prompt */
  private def commandSc(sc:String) = {
    Print.simple(Colors.autoPrompt + "script> " + Colors.end + sc)
    t.command(sc)
  }

}

object ScriptEngine {

  /** formats the script file into runnable entity. TODO: better imports filter */
  def prepare(raw : String) : List[String] =
    raw.replaceAll("/\\*(.|\\n)+?\\*/", "\n") // remove comments with /* */
      .split('\n')
      .filterNot(_.trim.isEmpty) // remove empty lines
      .toList
      .map(_.trim)

  /** list files in resources/scripts/ dir in the JAR */
  def getJarScripts() : List[String] = {
    val src = getClass.getProtectionDomain().getCodeSource()
    if (src != null) {
      val jar = src.getLocation //getClass.getResource("/scripts/")
      val zip : ZipInputStream = new ZipInputStream(jar.openStream())
      /* Now examine the ZIP file entries to find those you care about. */
      var ze : ZipEntry = null
      val buffer  = mutable.Buffer[String]()
      while( { ze = zip.getNextEntry(); ze } != null ) {
        if (ze.getName().contains("scripts") )
          buffer.append(ze.getName())
      }
      return buffer.toList.filter(s => !"""(?<=scripts/).+(?=\.sc)""".r.findFirstIn(s).isEmpty)
    } else return Nil
  }

}
