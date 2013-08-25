import com.misiunas.klab.gui.show.Show
import com.misiunas.klab.io.Load
import com.misiunas.klab.track.analysis.{Transition, PosHistogram}
import com.misiunas.klab.track.assemblies.TrackAssembly
import com.misiunas.klab.track.corrections.Filter
import com.misiunas.klab.track.geometry.Channel

/**
 * == analysis script for 1D channel ==
 *
 * User: karolis@misiunas.com
 * Date: 30/07/2013
 * Time: 16:00
 */

//Plan:
//  1. get file (be ask two times)
//  2. filter out single occurrences (tracking errors)
//  3. estimate the histogram and return it
//  4. find translocations



val raw = TrackAssembly(Load());
println("loaded: "+raw)

val channel = Channel.simpleAlongX(5, 95, 40);

val corr: TrackAssembly = Filter.byContinuity(Filter.byLocation(Filter.bySize(raw), channel), channel);
println("filtered: "+corr)



val r = Range(5,96,2).toList.map(_.toDouble);

Show(PosHistogram(corr, r, _.x))

println(Transition(corr, channel))