package klab.track.infreastructure.tracks

/**
 * == Additional information contained in the track ==
 *
 *
 * User: karolis@misiunas.com
 * Date: 19/08/2013
 * Time: 16:29
 */
trait TrackInfo {

  /** physical time track was acquired or put into the system */
  def time: Long

  /** Experiment title that the track was done in */
  def experiment: String

  /** Specific comments about this track */
  def comment: String

  /** units for each Pos coordinate */
  def units: List[String]

  /** The software version of this track.
    * Know the version with which this particle track was created.
    */
  def version: Int

  // ------ Implemented methods ------

  /** separator between comments */
  final val commentSeparator: String = ";"

  /** list comments by separating at specified commentSeparator */
  def commentList: Array[String] = comment.split(commentSeparator).map( _.trim ).filterNot(_.isEmpty)
}
