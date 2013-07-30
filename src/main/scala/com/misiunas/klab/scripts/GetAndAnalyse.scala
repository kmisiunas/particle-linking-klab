package com.misiunas.klab.scripts

import com.misiunas.klab.track.assemblies.TrackAssembly
import com.misiunas.klab._
import com.misiunas.klab.track.corrections.Filter
import com.misiunas.klab.track.geometry.{Channel, Point, Box2D}
import com.misiunas.klab.track.analysis.{Transition, PosHistogram}

/**
 * == script for loading data from file and getting it analysed ==\
 *
 * //TODO: scripting infrastructure wanted!
 *
 * User: karolis@misiunas.com
 * Date: 24/07/2013
 * Time: 23:02
 */
object GetAndAnalyse {

  def run : TrackAssembly = {
    println("""Starting automated script "GetAndAnalyse"
    Plan:
    1. get file (be ask two times)
    2. filter out single occurrences (tracking errors)
    3. estimate the histogram and return it
    4. find translocations
    """)

    var file :String = io.fileChooser
    if(file.isEmpty || file == "null" || file == "nullnull") file = io.fileChooser
    if(file.isEmpty || file == "null" || file == "nullnull") return null

    val raw = TrackAssembly(io.Load.loadString(file))
    println("loaded: "+raw)

    val channel = Channel.simpleAlongX(5, 95, 40)
    val corr: TrackAssembly = Filter.byContinuity(Filter.byLocation(Filter.bySize(raw), channel), channel)
    println("filtered: "+corr)

    //val corr = com.misiunas.klab.track.corrections.Continuum.autoCorrection(pt)

    val r = Range(5,96,2).toList.map(_.toDouble)
    PosHistogram(corr, r, _.x).show()

    println(Transition(corr, channel))

    return corr
  }



}
