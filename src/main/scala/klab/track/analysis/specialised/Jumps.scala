package klab.track.analysis.specialised

import com.misiunas.geoscala.Point
import klab.track.ParticleTrack
import breeze.linalg.DenseVector
import klab.track.operators.{Bin, TwoTracks}
import klab.track.operators.TwoTracks.PairInteraction
import klab.track.geometry.position.{LQPos, Pos}
import klab.track.analysis.Diffusion

/**
 * == Analyse Jumps ==
 *
 * Features:
 *  -
 *
 *
 * User: kmisiunas
 * Date: 11/12/2013
 */
object Jumps {


  /** Finds jump magnitude of coexisting tracks at specified time
    *
    * Returns:
    *  - "left" particle jump magnitude
    *  - "right" particle jump magnitude
    *
    * Warning: method not efficient! */
  def twoParticleAt(along: Point => Double, at: Double, binSize: Double = 1.0):
    Iterable[ParticleTrack] => Map[String, DenseVector[Double]] = ta => {

    val interactions: List[PairInteraction] = TwoTracks.findTwoParticleInteractions()(ta)

    val bin = breeze.linalg.linspace(at - binSize/2, at + binSize/2, 2)
    def inBin(x: Double): Boolean = x >= bin(0) && x < bin(1)

    def distance(p1: Point, p2: Point) = (along(p1) - along(p2)).abs

    val atRightDistance = // find only tracks at the right distance
      interactions.map(
        pi => pi.map( _.filter( p => inBin( distance(p._1,p._2) ) ) ) // remove elements not at right distance
      )
      .filterNot( _.interact.isEmpty )
      .flatMap( pi => pi.interact.map( pos => pi.copy(List(pos)) ) ) // separate them into separate interact elements
      .filter( pi => pi._1.hasNextQualityFrame(pi.interact.head._1) ) // remove ones without next step
      .filter( pi => pi._2.hasNextQualityFrame(pi.interact.head._1) ) // remove ones without next step
      .sortBy( _.interact.head._1.t ) // sort by time


    /** takes filtered data and applies specified to it */
    def analyse(data: List[PairInteraction])
               (using: Map[String, PairInteraction => Double]): Map[String, DenseVector[Double]] = {
      val res = using.map( m => (m._1 , DenseVector.zeros[Double](data.length)) ).toMap
      /** function for computing and placing within 'res' */
      def place(pi: PairInteraction, at: Int): Unit =
        using.map( m => (m._1 , m._2(pi)) )
          .foreach( t => res(t._1).update(i = at, v = t._2) )
      data.zipWithIndex.foreach( p => place(p._1, p._2) )
      res
    }

    analyse(atRightDistance)(operations) + ("bin" -> bin)
  }

  /** List of operations to perform
    * Specs:
    *  - PairInteractions should have a list with only one element at which distance the particles are
    */
  val operations: Map[String, PairInteraction => Double] = Map(
    "meanX" -> (pi => {
      val pos = pi.interact.head
      (pos._1 + pos._2).x / 2
    }),

    "leftId" -> (pi => {
      val pos = pi.interact.head
      if (pos._1.x < pos._2.x) pi._1.id else pi._2.id
    }),

    "rightId" -> (pi => {
      val pos = pi.interact.head
      if (pos._1.x > pos._2.x) pi._1.id else pi._2.id
    }),

    "time" -> (pi => {
      pi.interact.head._1.t
    }),

    "leftJump" -> (pi => {
      val pos = pi.interact.head
      val posAndTrack = if (pos._1.x < pos._2.x)  (pos._1, pi._1) else (pos._2, pi._2)
      val p2 = posAndTrack._2.nextFame( posAndTrack._1 ).get
      (p2 - posAndTrack._1).x
    }),

    "rightJump" -> (pi => {
      val pos = pi.interact.head
      val posAndTrack = if (pos._1.x > pos._2.x)  (pos._1, pi._1) else (pos._2, pi._2)
      val p2 = posAndTrack._2.nextFame( posAndTrack._1 ).get
      (p2 - posAndTrack._1).x
    }),

    "leftX" -> (pi => {
      val pos = pi.interact.head
      if (pos._1.x < pos._2.x) pos._1.x else pos._2.x
    }),

    "rightX" -> (pi => {
      val pos = pi.interact.head
      if (pos._1.x > pos._2.x) pos._1.x else pos._2.x
    }),

    "leftDx" -> (pi => {
      val pos = pi.interact.head
      val posAndTrack = if (pos._1.x < pos._2.x)  (pos._1, pi._1) else (pos._2, pi._2)
      val list = posAndTrack._2.atTime( posAndTrack._1.t , posAndTrack._1.t + 1 ) // take 3 elements
      Diffusion.naive_Di(list) match {
        case d :: _ => d.Di.x
        case _ => Double.NaN
      }
    }),

    "rightDx" -> (pi => {
      val pos = pi.interact.head
      val posAndTrack = if (pos._1.x > pos._2.x)  (pos._1, pi._1) else (pos._2, pi._2)
      val list = posAndTrack._2.atTime( posAndTrack._1.t , posAndTrack._1.t + 1 ) // take 3 elements
      Diffusion.naive_Di(list) match {
        case d :: _ => d.Di.x
        case _ => Double.NaN
      }
    })
  )


}
