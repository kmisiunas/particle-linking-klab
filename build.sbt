
name := "KLab"

version := "0.1.8"

scalaVersion := "2.10.4"

// -------- Assembly Config ---------
// Use: assembly
// Use: assembly-package-dependency
// Use: assemblyPackageDependency

// --------- Pack plugin config -----
// https://github.com/xerial/sbt-pack

packSettings

// [Optional: Mappings from a program name to the corresponding Main class ]
packMain := Map("KLab" -> "klab.KLab")

// [Optional] JVM options of scripts (program name -> Seq(JVM option, ...))
packJvmOpts := Map("KLab" -> Seq("-Xms1g","-Xmx2G") )

packGenerateWindowsBatFile := false

// crucial for pack to be runnable - unknown error otherwise
// packPreserveOriginalJarName := true
packJarNameConvention := "original"

// ----------- Libraries ---------------

// ----------- Main ----------------

// GeoScala
libraryDependencies += "com.misiunas" %% "geoscala" % "0.1.3"

// BREEZE - https://github.com/scalanlp/breeze/
libraryDependencies ++= Seq(// other dependencies here
        "org.scalanlp" % "breeze_2.10" % "0.7",
        // native libraries are not included by default. add this if you want them (as of 0.7-SNAPSHOT)
        // native libraries greatly improve performance, but increase jar sizes.
        "org.scalanlp" % "breeze-natives_2.10" % "0.7" )

resolvers ++= Seq(
  // other resolvers here
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)

// TEST - http://www.scalatest.org/
libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.3" % "test"

// Joda time for scala - https://github.com/nscala-time/nscala-time
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.0.0"

// Play JSON library: http://www.playframework.com/documentation/2.2.x/ScalaJson
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.2.1"

// -------------- Use these -------------

// logging https://github.com/typesafehub/scala-logging
libraryDependencies += "com.typesafe.scala-logging"  %% "scala-logging-slf4j" % "2.0.4"


// ------------------ GUI -----------------

// library for GUI designer in Intellij - needed if one does not get attached by intellij ieda
libraryDependencies += "com.intellij" % "javac2" % "7.0.3"

// allows interaction with terminal? -> does not work
//libraryDependencies <+= scalaVersion { "org.scala-lang" % "jline" % _ }
libraryDependencies += "org.scala-lang" % "jline" % "2.10.4"

// Adds compiler that is needed for REPL
libraryDependencies +=  "org.scala-lang" % "scala-compiler" % "2.10.4"

// Breeze visualisation - https://github.com/scalanlp/breeze-viz
// libraryDependencies += "org.scalanlp" % "breeze-viz_2.10" % "0.5.2"


// ------------- Unnessasary ---------------

// For Breeze
// libraryDependencies  += "com.github.fommil.netlib" % "all" % "1.1.1" pomOnly()

// Breeze visualization - https://github.com/scalanlp/breeze-viz
//libraryDependencies += "org.scalanlp" % "breeze-viz_2.10" % "0.5.2"

// JUNIT - not sure if needed separatley?
// libraryDependencies += "junit" % "junit" % "4.10" % "test"

// Library for monitoring performance
//libraryDependencies += "nl.grons" %% "metrics-scala" % "3.0.0"

// needed for console interface to work properly
//libraryDependencies += "jgoodies" % "forms" % "1.0.5"

//libraryDependencies += "com.jgoodies" % "jgoodies-common" % "1.4.0"

// Remove latter, use Java swing for GUI
//libraryDependencies <+= scalaVersion { "org.scala-lang" % "scala-swing" % _ }

// ScalaInterpreterPane - a Way ti interact with KAnalysis: https://github.com/Sciss/ScalaInterpreterPane
// libraryDependencies += "de.sciss" %% "scalainterpreterpane" % "1.4.+"

// JFreeChart for scala: https://github.com/wookietreiber/scala-chart
// wrapped in scala: https://github.com/wookietreiber/scala-chart
// libraryDependencies += "com.github.wookietreiber" %% "scala-chart" % "latest.integration"

// Json: https://github.com/lift/lift/tree/master/framework/lift-base/lift-json
//libraryDependencies += "net.liftweb" %% "lift-json" % "2.5.1"