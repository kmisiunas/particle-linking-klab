import AssemblyKeys._ // put this at the top of the file

assemblySettings

// your assembly settings here

//mainClass in assembly := Some("com.klab.package")

jarName in assembly := "KLab.jar"

// Produces a jar without dependencies and scala language jar included
// assemblyOption in assembly ~= { _.copy(includeScala = false) }

//assemblyOption in assembly ~= { _.copy(includeBin = false) }



//packageOptions in assembly += Package.ManifestAttributes("SplashScreen-Image" -> "splash.png")


// Known problems:
// - chart package included twice
// - commons included twice

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
  case "META-INF/DEPENDENCIES" => MergeStrategy.discard
  case "META-INF/MANIFEST.MF" => MergeStrategy.discard
  case "META-INF/BCKEY.DSA" => MergeStrategy.discard
  case "META-INF/BCKEY.SF" => MergeStrategy.discard
  case "META-INF/NOTICE.txt" => MergeStrategy.discard
  case "META-INF/NOTICE" => MergeStrategy.discard
  case "META-INF/LICENSE.txt" => MergeStrategy.discard
  case PathList("org", "fusesource", xs @ _*) => MergeStrategy.last
  case PathList("META-INF", "native", xs @ _*)=> MergeStrategy.last
  case "rootdoc.txt" => MergeStrategy.first
  case PathList("com", "intellij", "uiDesigner", xs @ _*) => MergeStrategy.first
  case PathList("com", "jgoodies", "forms", xs @ _*) => MergeStrategy.first
  case PathList("com", "keypoint", xs @ _*) => MergeStrategy.first
  case PathList("org", "jfree", xs @ _*) => MergeStrategy.first
  case _ => MergeStrategy.deduplicate
}
}
