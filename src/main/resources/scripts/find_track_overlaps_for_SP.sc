import klab.io.{Save, Import, Path}
import klab.track.corrections.specialised.Confinement

/**
 * == Script for finding overlaps in tracks ==
 *
 * The script was designed for data provided by Stefano
 *
 * Saves the file in the same directory that the file came from
 *
 * Script rules: it it contains ";" at the end the result will not be printed
 *
 * Designed for version 0.1.5
 *
 * User: karolis@misiunas.com
 * Date: 05/08/2013
 */

println("Please choose the directory (with subdirectories) containing the files with tracks")

val file = Path.find()

val raw = Import.dirToAssembly(file, "finding overlaps");

println("loaded: "+raw)

val overlaps = klab.track.corrections.Confinement.findOverlaps( _.x )( raw );

println("found " + overlaps.size + " overlaps")

if (!overlaps.isEmpty) Save(overlaps.flatMap(_.atTimes).sorted.map(_.toString).toList, Path(file).dir + "overlap_frames.txt")

if (!overlaps.isEmpty)println("saved frames to " + Path(file).dir + "overlap_frames.txt")


