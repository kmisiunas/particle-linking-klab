import klab.io.{Save, Import, Path}

/**
 * == Tracking script ==
 *
 * load pos files from video analyis, forlmat them, save to matlab, run matlab tracking and import finished files.
 *
 *
 * Created by kmisiunas on 07/01/2014.
 */

// step 1: select root directory
val root = Path.find

// step 2: load data
val posList = Import.dirToListPos(root);

// step 3: save data for matlab tracking and do tracking
Save(posList.map(p => p.x+","+p.y+","+p.t+","), "/Users/kmisiunas/Dropbox/PhD/Software/Matlab_Tracking/tracking/autoLoad/input.csv" )

val process = Runtime.getRuntime().exec("/Users/kmisiunas/Dropbox/PhD/Software/Matlab_Tracking/tracking/doMatLabTracking")
process.waitFor()

// step 5: load data into track assembly
val raw = Import.dirToAssembly("/Users/kmisiunas/Dropbox/PhD/Software/Matlab_Tracking/tracking/autoSave/")

//step 6: save to parent dir
Save(raw, Path(root).parent + "TrackAssembly_XX.json")


