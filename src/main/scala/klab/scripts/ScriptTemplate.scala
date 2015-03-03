package klab.scripts

import klab.KLab.{AppConfig}

/**
 * Remember to import Print.println to get colored printing
 *
 * Created by kmisiunas on 15-03-01.
 */
abstract class ScriptTemplate {

  /** list of names that allows to invoke this script with */
  def name: String

  /** description and configuration options */
  def config(op: scopt.OptionParser[AppConfig]): Unit

  /** runs the script */
  def run(options: AppConfig)

}


object ScriptTemplate {

  lazy val list: List[ScriptTemplate] = List(
    new Track
  )
}