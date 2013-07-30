package com.misiunas.klab.gui.repl

/**
 * == command line color scheme for terminal ==
 *
 * User: karolis@misiunas.com
 * Date: 30/07/2013
 * Time: 14:54
 */
class Colors {

  /** terminator for selected colors */
  def end = "\\x1b[0m"

  /** error message */
  def error = "\\x1b[31m"

  def prompt = "\\033[36m"

}
