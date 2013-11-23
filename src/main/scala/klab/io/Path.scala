package klab.io

import java.io.File
import java.net.URI
import com.alee.laf.filechooser.WebFileChooser

/**
 * == Tool for quick navigation between the files and folders ==
 *
 * Objectives:
 *  - User friendly
 *  - good for scripting
 *  - scala friendly
 *
 * Design specs:
 *  - Immutable objects
 *  - Easy move between object, string and java Path
 *  - path change tools: file name, extension, full name, dir, superDir...
 *  - Constructors based on object
 *  - store dirs with end "/"
 *
 * User: karolis@misiunas.com
 * Date: 06/08/2013
 * Time: 12:01
 */
class Path private (private val path: String) {

  // ---------- return properties -------------

  def fileName: String =
    if (isFile) {
      val woEnd = """^.*(?=(\..{1,5}$))""".r.findFirstIn(name)
      if (woEnd.isEmpty) return ""
      else woEnd.get
    }
    else ""

  def name: String = toFile.getName

  def dirName: String = dir.toFile.getName

  /** returns the relative path to the file or dir with respect to working dir */
  def relative(): String = ???

  /** Returns extension of the file, if a dir, it returns / */
  def extension: String = name.diff(fileName).drop(1)

  def isFile: Boolean = !isDir

  def isDir: Boolean = path.endsWith("/")

  def exists: Boolean = toFile.exists()

  /** returns path difference between two paths */
  def diff(p: Path): String = this.toString.diff(p.toString)

  // ---------- Manipulation Methods ----------

  def dir: Path = Path( """^.*/""".r.findFirstIn(path).get )

  /** returns parent directory */
  def parent: Path = if (this == root) root else Path( dir.toFile.getParent )

  def root: Path = Path( """^.*?/""".r.findFirstIn(path).get )

  /** list all object in the current dir */
  def list: List[Path] = if(dir.exists) dir.toFile.listFiles().toList.map(f => Path(f.getAbsolutePath) ) else Nil

  def listFiles: List[Path] = list.filter( _.isFile )

  def listDirs: List[Path] = list.filter( _.isDir )

  def cd(s: String): Path = Path( dir.toString + s )

  // ---------- Methods dealing with files and dirs ----------

  /** Deletes file or dir where the path is pointing (if they exist) */
  def delete(confirmPrompt: Boolean = true): Path = ???

  /** Creates a file or a path if it does not already exist */
  def create(name: String): Path = ???

  /** Rename current file or dir */
  def rename(newName: String): Path = ???

  /** Rename current file or dir */
  def move(toPath: Path): Path = ???

  /** Makes a copy in specified dir */
  def copy(toPath: Path): Path = ???

  // ---------- Conversion methods ------------

  override def toString: String = path

  def toFile: java.io.File = new File(path)

  // toJPath? - might be confusing!

  def toURI: URI = toFile.toURI

  // ------------ Other ---------------

  override def equals(obj: Any): Boolean = obj match {
    case p:Path => p.toString == this.toString
    case s:String => s == this.toString
    case _ => false
  }

  /** sets current dir as the working directory */
  def setWork(): Path = { Path.work = dir; Path.work }
}

object Path {

  val separator = '/' // always use UNIX separator

  /** implicit conversion to string */
  implicit def pathToString(p: Path) = p.toString

  def apply(path: String): Path = new Path(checkPath(path))

  def apply(): Path = Path.work

  lazy val root: Path = Path.user.root

  /** default user directory */
  lazy val user: Path = Path( System.getProperty("user.dir") )

  /** current working folder */
  var work: Path = user

  /** checks formatting of provided path provided. Fixes it if it does not conform to expected norm */
  def checkPath(file: String): String = {
    val f = file.trim.replace('\\', separator)
    val fO = (new File(f)) // not very fast?
    if (fO.isDirectory && f != separator) fO.getAbsolutePath + separator else fO.getAbsolutePath
  }

  /** Find the path using GUI interface */
  def find(): String = {
    val fileChooser = new WebFileChooser(lastPath)
    fileChooser.setMultiSelectionEnabled( false )
    fileChooser.showOpenDialog( null )
    val file = fileChooser.getSelectedFile()
    lastPath = Path(file.getAbsolutePath).dir
    file.getAbsolutePath
  }

  private var lastPath: String = Path.work

}
