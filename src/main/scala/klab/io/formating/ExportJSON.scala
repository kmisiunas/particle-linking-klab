package klab.io.formating

import play.api.libs.json.{Json, JsValue}

/**
 * == can export class as JSON ==
 *
 * User: karolis@misiunas.com
 * Date: 20/08/2013
 * Time: 17:48
 */
trait ExportJSON {

  /** Produces a readable JSON file. Implementation should be modular. */
  def toJson: String = Json.prettyPrint( toJsonValue )

  /** Stream - important to override for very large objects! */
  def toJsonIterator: Iterator[String] = Iterator(toJson)

  /** creates Json Value */
  def toJsonValue: JsValue

}
