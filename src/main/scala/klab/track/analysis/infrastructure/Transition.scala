package klab.track.analysis.infrastructure

import klab.track.formating.{ExportCSV, CompatibleWithJSON}
import org.joda.time.DateTime
import klab.track.assemblies.Assembly
import klab.track.geometry.Channel
import klab.track.ParticleTrack
import scala.annotation.tailrec
import klab.track.geometry.position.Pos

/**
 * == Class for analysing transition across the channel ==
 *
 * Show compute:
 *  - transition type (forward, backward, forward return, backward return)
 *  - time spent in the channel
 *
 *  Currently tracks are broken up.
 *
 * User: karolis@misiunas.com
 * Date: 25/07/2013
 * Time: 21:35
 */
class Transition private (val list : List[Transition.ResTransition],
                          val experiment: String,
                          val channel: Channel,
                          val analysisParam: String)
  extends ExportCSV{

  lazy val csvHeader = {
    val timeUnit = list.head.track.units(0)
    "Track_id" + csvSeparator + "Transition_type_no" + csvSeparator +
      "Transition_type_name"+ csvSeparator + "Time_in_channel_("+ timeUnit +")" + csvSeparator  +
      "Time_entered_channel_("+ timeUnit +")" + csvSeparator + "Time_left_channel_("+ timeUnit +")" + csvSeparator
  }

  /** generate analysis statement */
  lazy val analysisStatement: String = {
    "Channel Transition analysis report \n" +
    "---\n" +
    "Experiment: " + experiment + "\n" +
    "Analysis Time: " + DateTime.now.toString("H:m Y-M-d")+ "\n" +
    "Channel: " + channel.mkString + "\n" +
    "Units: " + list.head.track.units.mkString(", ") + "\n" +
    "Analysis parameters: " + analysisParam + "\n" +
    "Analysis summary:\n" +
    Transition.listResultTypes.map(resType => {
      val events = list.filter(_.transition eq resType)
      formatStatement(resType.description +"("+resType.shortName+")",
                      events.size,  // number of events of given type
                      events.map(_.timeInChannel).sum / events.size) // mean time in channel
    }).mkString
  }

  private def formatStatement(name: String, tr: Int, dt: => Double): String =
    name + ": count="+tr + (if(tr>0) (", mean time="+dt+"\n") else "\n")


  override def toString: String = "Transition#analysis(size=" + size + ")"

  def size = list.size

  def toCSV: String = csvHeader + "\n" + list.map(_.toCSV).mkString("\n")

  def mkString: String = analysisStatement

}




object Transition {

  /** special class for storing the different types of events that can happen */
  class ResTransitionType(val identification: Int,
                          val description: String,
                          val shortName: String )

  final val listResultTypes: List[ResTransitionType] = List(
    new ResTransitionType(-2, "Backward Transitions", "BT"),
    new ResTransitionType(-1, "Backward Returns", "BR"),
    new ResTransitionType( 0, "Incomplete tracks", "I"),
    new ResTransitionType( 1, "Forward Returns", "FR"),
    new ResTransitionType( 2, "Forward Transitions", "FT")
  )

  /** The result of transition is stored as a special object */
  class ResTransition (val track: ParticleTrack,                // track that was analysed
                       val transition: ResTransitionType,       // type of the event
                       val timeInChannel: Double,               // time spent in the channel
                       val timeInterval: (Double, Double))      // time range within the channel
    extends ExportCSV{

    def toCSV: String = track.id + csvSeparator + transition.identification + csvSeparator +
      transition.shortName + csvSeparator + timeInChannel + csvSeparator  +
      timeInterval._1 + csvSeparator + timeInterval._2 + csvSeparator
  }

  /** analyse given particle assembly
    * @param minSize minimum number of points before track is considered
    */
  def apply(ta: Assembly, ch: Channel, minSize: Int = 10): Transition = {

    def analyseTrack(pa: ParticleTrack): List[ResTransition] =
      Find.segmentsWithin(ch)(pa).
        filter( _.size >= minSize ).
        map(seg => new ResTransition(pa, classify(seg), seg.last.t - seg.head.t, (seg.head.t, seg.last.t) ) )

    /** classifies track transition */
    def classify(list: List[Pos]): ResTransitionType = {
      val beginning = list.head
      val end = list.last
      (ch.isInBeginning(beginning), ch.isInEnding(beginning), ch.isInBeginning(end), ch.isInEnding(end)) match {
        case (false,true,true,false) => listResultTypes(0)
        case (false,true,false,true) => listResultTypes(1)
        case (true,false,true,false) => listResultTypes(3)
        case (true,false,false,true) => listResultTypes(4)
        case _ => listResultTypes(2)
      }
    }

    @tailrec
    def iterate(ta: List[ParticleTrack], acc: List[ResTransition] = Nil) : List[ResTransition] =
      if (ta.isEmpty) return acc.sortBy(_.track.id)
      else iterate( ta.tail, analyseTrack(ta.head) ::: acc )

    new Transition( iterate(ta.toList), ta.experiment, ch,
      "segmented analysis; tracks smaller that size=" + minSize + " were discarded from Transition an." )
  }
}
