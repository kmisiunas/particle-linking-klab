import klab.io.{Import, Path}

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

println("Please choose the directory (with subdirectories) containing the files with tracks");


val file = Path.find()

val raw = Import.dirToAssembly(file, "finding overlaps");

println("loaded: "+raw);

val overlaps = klab.track.corrections.Confinement.findOverlaps( _.x )( raw )

println("loaded: "+raw);

val filtered: TrackAssembly = joint apply Filter.bySize(min=5) apply Filter.byLocation(channel)

val corr: TrackAssembly = filtered apply Confinement.autoCorrection(channel) apply Continuum.fixEnds(channel)

val r = Range(5,96,1).toList.map(_.toDouble);

val hist = PosHistogram(corr, r, _.x);
Show( hist )
Save(hist.toCSV, Path(file).dir.cd("analysis_histogram.csv"))

val trans = Transition(corr, channel, minSize = 3);
println(trans.mkString)
Save(trans.mkString, Path(file).dir.cd("analysis_transition.txt"))
Save(trans.toCSV, Path(file).dir.cd("track_transitions.csv"))