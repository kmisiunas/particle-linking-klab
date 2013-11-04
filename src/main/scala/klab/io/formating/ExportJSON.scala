package klab.io.formating

/**
 * == can export class as JSON ==
 *
 * User: karolis@misiunas.com
 * Date: 20/08/2013
 * Time: 17:48
 */
trait ExportJSON {

  /** Produces a readable JSON file. Implementation should be modular. */
  def toJSON: String

}
