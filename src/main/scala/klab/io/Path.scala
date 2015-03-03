package klab.io

import java.io.File
import java.net.URI
import com.alee.laf.filechooser.WebFileChooser
import klab.KLab
import klab.gui.Print

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
 *  - Format windows paths into unix ones
 *  - operator / as automatic path addition
 *
 * User: karolis@misiunas.com
 * Date: 06/08/2013
 * Time: 12:01
 */
class Path private (private val path: String) extends Ordered[Path] {

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

  /** operator that allows to extend current Path with subfile or dir  */
  def / (p: String) = Path(dir.toString + p)

  def dir: Path = Path( """^.*/""".r.findFirstIn(path).get )

  /** returns parent directory */
  def parent: Path = if (this == root) root else Path( dir.toFile.getParent )

  def root: Path = Path( """^.*?/""".r.findFirstIn(path).get )

  /** list all object in the current dir */
  def list: List[Path] = if(dir.exists) dir.toFile.listFiles().toList.map(f => Path(f.getAbsolutePath) ) else Nil

  def listFiles: List[Path] = list.filter( _.isFile )

  def listDirs: List[Path] = list.filter( _.isDir )

  /** lists all files here and in sub-dirs */
  def listDeepFiles: List[Path] = listFiles ::: listDirs.flatMap(_.listDeepFiles)

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

  override def compare(x: Path): Int = path.compare( x.toString )

  // ----------- Graphical --------------

  def ls: Unit = this.list.sorted.foreach( Print.apply(_) )
}

object Path {

  val separator = '/' // always use UNIX separator

  /** implicit conversion to string */
  implicit def pathToString(p: Path) = p.toString

  def apply(path: String): Path = new Path(checkPath(path))

  def apply(path: File): Path = apply( path.toString )

  def apply(): Path = Path.work

  /** returns path that KLab was executed at */
  def current: Path = Path( new java.io.File( "." ).getCanonicalPath )

  lazy val root: Path = Path.user.root

  /** default user directory */
  lazy val user: Path = Path( System.getProperty("user.dir") )

  /** current working folder */
  var work: Path = user
  def work_=(dir: String): Path = {work = Path(dir).dir; work}

  /** the path to this program */
  val klab: Path = Path(KLab.getClass.getProtectionDomain().getCodeSource().getLocation().getPath())

  /** checks formatting of provided path provided. Fixes it if it does not conform to expected norm */
  def checkPath(file: String): String = {
    val f = file.trim match {
      case s if s.startsWith("~" + File.separator) => System.getProperty("user.home") + file.trim.drop(1)
      case s => s
    }
    val fO = new File(f)
    // create a unix like path String with / at the end for directories
    fO.getAbsolutePath.replace('\\', separator) +
      (if (!fO.exists() && f.last == '/') separator else if(!fO.isDirectory || f == separator) "" else separator)
  }

  /** Find the path using GUI interface */
  def find(): String = {
    val fileChooser = new WebFileChooser(lastPath)
    fileChooser.setMultiSelectionEnabled( false )
    fileChooser.showOpenDialog( null )
    val file: File = fileChooser.getSelectedFile()
    if (file == null) throw new RuntimeException("User canceled the operation")
    lastPath = Path(file.getAbsolutePath).dir
    Path(file.getAbsolutePath).toString
  }

  private var lastPath: String = Path.work

}
