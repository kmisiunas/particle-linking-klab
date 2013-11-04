package klab.gui.repl

/**
 * == command line color scheme for terminal ==
 *
 * todo: change entire model for coloring
 *
 * User: karolis@misiunas.com
 * Date: 30/07/2013
 * Time: 14:54
 */
class Colors {

  /** terminator for selected colors */
  def end = "\033[0m"

  /** error message */
  def error = "\033[31m"

  def prompt = "\033[36m"

  /** color when messages are printed automatically */
  def autoPrompt = "\033[36m"

  /** color when messages are printed automatically */
  def print = "\033[33m"

}
