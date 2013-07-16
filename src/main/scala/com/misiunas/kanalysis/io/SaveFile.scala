package com.misiunas.kanalysis.io

import com.misiunas.kanalysis.track.{TrackAssembly, ParticleTrack}
import net.liftweb.json.JsonDSL._
import java.io.File

/**
 * User: karolis@misiunas.com
 * Date: 12/07/2013
 * Time: 15:53
 */
object SaveFile {

  def save(list : Seq[String], file: String):Unit = {
    val f = new File(file)
    if (!f.getParentFile().exists()) f.getParentFile().mkdirs()
    if (!f.exists()) f.createNewFile()
    val p = new java.io.PrintWriter(f)
    try {
      list.foreach(p.println)
    }
    finally { p.close() }
  }

  def save(string: String, file: String):Unit = save(List(string), file)

  /** Saves the Particle track in JSON format to a "file". If dir is provided, it will use default file name: Track_id.json */
  def save(pt: ParticleTrack, file: String):Unit = {
    val filePath = if(file.trim.endsWith(".json")) file else formatDir(file)+"Track_"+pt.id+".json"
    save(pt.mkString, filePath)
  }

  /** Saves the TrackAssembly object in JSON format. If dir is provided, it will use default file name: Track_Assembly.json */
  def save(ta: TrackAssembly, file: String):Unit = {
    val filePath = if(file.trim.endsWith(".json")) file else formatDir(file)+"Track_Assembly.json"
    save(ta.mkString, filePath)
  }

  /** make sure the dir path is correctly passed */
  private def formatDir(dir:String) = if(dir.endsWith("/")) dir.trim else dir.trim + "/"


}
