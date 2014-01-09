package klab.io

/**
 * Created by kmisiunas on 07/01/2014.
 */
object MatLab {

  var pathToMatLab: String = "/Applications/MATLAB_R2013a.app/bin/"

  val commandTemplate: String = """matlab -nodesktop -nosplash -r "path(path, '@path');@script;quit;" """

  // todo: initial testing did not produce good results while being run from terminal

  /** executes a matlab script */
  def apply(scriptPath: String): String = {
    val path: String = "/([^/]+?)$".r.replaceAllIn(scriptPath, "")
    val cmdPath = "@path".r.replaceAllIn(commandTemplate, path )
    val scriptName = "/([^/]+?)$".r.findFirstIn(scriptPath).get.drop(1).replace(".m", "")
    val cmd: String = "@script".r.replaceAllIn(cmdPath, scriptName)
    val process = Runtime.getRuntime().exec(pathToMatLab + cmd)
    //process.getOutputStream // todo: print and even input? = crash controll
    process.waitFor()
    pathToMatLab + cmd
  }

}
