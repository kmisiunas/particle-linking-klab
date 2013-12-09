package klab.io.formating

/**
 * == Indicator that object is compatible with JSON importing ==
 *
 * Created by kmisiunas on 09/12/2013.
 */
trait ImportJSON[T] {

  /** construct object form json string */
  def fromJson(json: String): T

  def formJson(json: Iterator[String]): T = fromJson(json.mkString("\n"))

}
