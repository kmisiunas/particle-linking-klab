package com.misiunas.klab.gui

import com.misiunas.klab.gui.repl.Terminal
import com.misiunas.klab.io.Load
import java.net.URL
import scala.io.Source
import java.nio.file.Paths
import java.io.File
import java.util.zip.{ZipEntry, ZipInputStream}
import scala.collection.mutable


/**
 * == Run automated scripts within the terminal ==
 *
 * User: karolis@misiunas.com
 * Date: 30/07/2013
 * Time: 15:57
 */
class ScriptEngine (private val t: Terminal){

  /** runs a script if it can be found in the terminal  */
  def run( script : String ) = {
    def process(s: String) =  execute(ScriptEngine.prepare(s))
    // if empty or "auto" do file open
    // if not a full path, do:
    // first check dir app/scripts/
    // then check packaged scripts in .scripts
    if(script == "" || script.toLowerCase == "auto") {
      println("Please select the script file (*.sc)")
      process(Load())
    }
    else if ((new File(script)).exists()) process(Load(script))
    else if ((new File("/scripts/"+script+".sc")).exists()) process(Load("/scripts/"+script+".sc"))
    else {
      val buildInScripts = ScriptEngine.getJarScripts()
      val sc = buildInScripts.find( _.contains("scripts/"+script+".sc") )
      if (!sc.isEmpty) {
        val raw = Source.fromInputStream(
          getClass.getResourceAsStream("/"+sc.get)
        ).getLines.mkString("\n")
        execute(ScriptEngine.prepare(raw))
      } else {
        println("Could not find script named "+script+". Please  place it in \"scripts/\" folder")
        println("Built in scripts are:")
        buildInScripts.foreach(f => println(" - "+ """(?<=scripts/).+(?=\.sc)""".r.findFirstIn(f).get ))
      }
    }
  }

  private def execute(list: List[String]) = list.foreach(t.commandSc(_))

}

object ScriptEngine {

  /** formats the script file into runnable entity. TODO: better imports filter */
  def prepare(raw : String) : List[String] =
    raw.replaceAll("/\\*(.|\\n)+?\\*/", "\n") // remove comments with /* */
      .split('\n')
      .filterNot(_.trim.isEmpty) // remove empty lines
      .filterNot(s => (s.contains("import") && !Imports.main.forall(!s.contains(_)))) // remove duplicate imports
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
        if (ze.getName().contains("scripts/") )
          buffer.append(ze.getName())
      }
      return buffer.toList.filter(s => !"""(?<=scripts/).+(?=\.sc)""".r.findFirstIn(s).isEmpty)
    } else return Nil
  }

}
