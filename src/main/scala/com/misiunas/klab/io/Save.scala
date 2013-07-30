package com.misiunas.klab.io

import java.io.File
import com.misiunas.klab.track.assemblies.{Assembly, TrackAssembly}
import com.misiunas.klab.track.ParticleTrack
import com.misiunas.klab.track.formating.CompatibleWithJSON
import scala.collection.GenTraversableOnce

/**
 * == Saving to Files object ==
 *
 * TODO: contains object cast - should be redesigned in log term. Using maps would help.
 *
 * User: karolis@misiunas.com
 * Date: 12/07/2013
 * Time: 15:53
 */
object Save {

  /** Saves any type of object to a specified file. If file name could not be determined will
      * save to a default /"output/" dir.
      */
  def apply(data: Any, file: String = "output/", kind:String = "auto") : Unit = {
      // if kind -> try saving with that kind
      // else try to determine kind and save with that kind
      // else save as text file
      val fp = formatFilePath(data, file, kind)
      kind.toLowerCase match{
        case "auto" | "automatic" => apply(data, fp, determineKind(data, fp))
        case "json" => write(data.asInstanceOf[CompatibleWithJSON[Any]].toJSON, fp)
        case "csv" => write(data.asInstanceOf[GenTraversableOnce[Any]].mkString(","), fp)
        case _ => write(data.toString, fp)
      }
    }

  /** A function for optimising file paths for the user automatically */
  def formatFilePath(data: Any, file: String, kind:String ) : String = {
    // check if the path is already correctly formatted
    val f = file.trim
    if(! """\.[a-zA-Z]{2,6}$""".r.findFirstIn(f).isEmpty) return f
    // if not determine file ending
    val fileEnd = data match {
      case d:CompatibleWithJSON[Object] => ".json"
      case d:GenTraversableOnce[Any] => ".csv"
      case _ => kind match {
        case "csv" => "csv"
        case _ => ".txt"
      }
    }
    // check if the file name was provided. if not create one
    val fileName = if(f.endsWith("/") || f.endsWith("\\")) {
      data match {
        case d:Assembly => "TrackAssembly_" + d.experiment
        case d:ParticleTrack => "ParticleTrack_"+d.id
        case d:Seq[Any] => "tmp_data"
        case _ => "tmp_output"
      }
    } else ""
    return f + fileName + fileEnd
  }

  private def determineKind(data: Any, ft: String): String = {
    data match {
      case d:CompatibleWithJSON[Object] => "json"
      case d:Seq[Any] => "csv"
      case _ => { // try to get from the file ending
        """\.[a-zA-Z]{2,6}$""".r.findFirstIn(ft).get.trim.toLowerCase match {
          case ".json" =>  "json"
          case ".csv" => "csv"
          case _ => "text"
        }
      }
    }
  }

  private def write(st :String, file: String) : Unit = {
    val f = new File(file)
    if (!f.getParentFile().exists()) f.getParentFile().mkdirs()
    if (!f.exists()) f.createNewFile()
    val p = new java.io.PrintWriter(f)
    try {
      p.print(st)
    }
    catch {
      case e:Exception => println("Error: could not write to file \""+file+"\" because: "+e)
    }
    finally { p.close() }
  }
}
