package klab.io

import java.io.File
import klab.track.assemblies.{Assembly, TrackAssembly}
import klab.track.Track
import klab.track.formating.CompatibleWithJSON
import scala.collection.GenTraversableOnce
import klab.io.formating.ExportJSON
import klab.io.infrastructure.save.SaveType
import org.joda.time.DateTime

/**
 * == Saving to Files object ==
 *
 * Usage:
 *  - Save( data , path/file , kind(optional) )
 *  - kind should indicate the type of file one wants to produce
 *
 * Features:
 *  - works with format handlers. New ones can be added by implementing SaveType and adding them to known list
 *
 * Version: 0.1.5
 * User: karolis@misiunas.com
 * Date: 12/07/2013
 */
object Save {

  /** Saves any type of object to a specified file. If file name could not be determined will
      * save to a default /"output/" dir.
      */
  def apply(data: Any, file: String = "output/", kind: String = ""): Unit = {
    val knowHandlers =
      if (kind.isEmpty) SaveType.getAll.sortBy( - _.priority)
      else SaveType.getAll.filter(kind.toLowerCase.trim == _.kind).sortBy( - _.priority)
    // method to find matching file in the list
    def findFirstHandler(list: List[SaveType]): Unit = {
      if (list.isEmpty) println("Data was NOT saved. Not a known type of data, consider passing default string constructor: data.toString")
      else if (list.head.isType(data)){
        val formatFile = formatFilePath(file, list.head)
        write(list.head.getWriter(data, formatFile), formatFile)
      } else findFirstHandler(list.tail)
    }
    findFirstHandler(knowHandlers)
  }


  /** A function for optimising file paths for the user automatically */
  private def formatFilePath(file: String, saveType: SaveType) : String = {
    // check if the file name was provided. if not create one
    val f = file.trim
    val fileName =
      if(f.endsWith("/") || f.endsWith("\\"))
        "raw_" + DateTime.now.toString("YYYY-MM-dd_HHmm")
      else ""

    // check if the path is already correctly formatted
    val fileExt = if("""\.[a-zA-Z]{2,6}$""".r.findFirstIn(f).isEmpty) saveType.defaultFileExtension
      else ""

    f + fileName + fileExt
  }


  private def write(st: Iterator[String], file: String): Unit = {
    if (st.isEmpty) return ()
    val f = new File(file)
    if (!f.getParentFile().exists()) f.getParentFile().mkdirs()
    if (!f.exists()) f.createNewFile()
    val p = new java.io.PrintWriter(f)
    try {
      while(st.hasNext) {
        p.println(st.next())
      }
    }
    catch {
      case e:Exception => println("Error: could not write to file \""+file+"\" because: "+e)
    }
    finally { p.close() }
  }
}
