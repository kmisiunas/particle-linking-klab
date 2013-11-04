package klab.io

import klab.track.ParticleTrack
import java.nio.charset.{StandardCharsets, Charset}
import java.nio.file.{Paths, Files}
import java.nio.ByteBuffer

/**
 * == Loading information from file ==
 *
 * TODO: needs a lot of work
 *
 * User: karolis@misiunas.com
 * Date: 12/07/2013
 * Time: 15:52
 */
object Load {

  /** syntactic sugar for the method */
  def apply(file: String) : String =  loadString(file)

  /** def auto loader from selected file prompt */
  def apply() : String = {
    var f = Path.find()
    if (f == null || f.isEmpty || f == "null" || f == "nullnull") f = Path.find()
    if (f == null || f.isEmpty || f == "null" || f == "nullnull") throw new Exception("Warning: user failed to pick a file")
    else apply(f)
  }


  /** SimpleGUI method to load file content into a string */
  def loadString(file: String, encoding : Charset = StandardCharsets.UTF_8) : String = {
    return encoding.decode(
      ByteBuffer.wrap(Files.readAllBytes(Paths.get(file)))
    ).toString();
  }

  /** file stream opener.
    * Still experimental - might run out of memory!
    * @return lines int the file */
  def open(file: String): List[String] = {
    val source = scala.io.Source.fromFile(file)
    val lines = source.getLines().toList
    source.close()
    lines
  }

}
