import com.misiunas.klab.gui.show.Show
import com.misiunas.klab.io.Load
import com.misiunas.klab.track.analysis.{Transition, PosHistogram}
import com.misiunas.klab.track.assemblies.TrackAssembly
import com.misiunas.klab.track.corrections.{Continuum, Filter}
import com.misiunas.klab.track.geometry.Channel

/**
 * == Analysis script for 1D channel with poor tracking routine ==
 *
 * The script was designed for data provided by Stefano
 *
 * The scripts assume that tracking routine occasionally looses particle position.
 * Also it can occasionally merges multiple particles (ex. see frame 41657 in practice set)
 *
 * Script rules: it it contins ";" at the end the result will not be printed
 *
 * User: karolis@misiunas.com
 * Date: 05/08/2013
 * Time: 16:56
 */

// Plan:
// - load TrackAssembly
// - Join up tracks within in a channel
// - filter tracks that are too small
// - filter tracks that are too close
// - filter tracks that are non-continuous
// - do std analysis

val raw = TrackAssembly(Load());

println("loaded: "+raw);

val channel = Channel.simpleAlongX(5, 95, 40);

val joint: TrackAssembly = raw apply Continuum.pairUp(channel)

val corr: TrackAssembly = joint apply Filter.bySize(5) apply Filter.byProximity(20, channel) apply Filter.byContinuity(channel)

//if (!Continuum.qualityCheck(corr)) throw new Exception("The assembly did not pass the quality check!")

val r = Range(5,96,2).toList.map(_.toDouble);

Show( PosHistogram(corr, r, _.x) )

println( Transition(corr, channel) )
