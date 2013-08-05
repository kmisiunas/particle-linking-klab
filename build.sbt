import AssemblyKeys._ // put this at the top of the file
import sbtassembly.Plugin._
//import xerial.sbt.Pack._

assemblySettings

name := "KLab"

organization := "com.misiunas"

version := "0.1.3"

scalaVersion := "2.10.2"


// Produces a jar without dependencies and scala language jar included
assembleArtifact in packageScala := false

assembleArtifact in packageDependency := false

//packageOptions in assembly += Package.ManifestAttributes("SplashScreen-Image" -> "splash.png")

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
    case _ => MergeStrategy.deduplicate
  }
}

//retrieveManaged := true // copies all the jars into lib_managed

// ----------- Libraries ---------------


// BREEZE - https://github.com/scalanlp/breeze/
libraryDependencies  += "org.scalanlp" % "breeze-math_2.10" % "0.4-SNAPSHOT"

resolvers ++= Seq(
            // other resolvers here
            // if you want to use snapshot builds (currently 0.4-SNAPSHOT), use this.
            "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
            "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/releases/"
)


libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

// Joda time for scala
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "0.4.2"

// Library for monitoring performance
//libraryDependencies += "nl.grons" %% "metrics-scala" % "3.0.0"

// Write good code: http://basis.reify.it/

// Database interaction: http://slick.typesafe.com/

// Json: https://github.com/lift/lift/tree/master/framework/lift-base/lift-json
libraryDependencies += "net.liftweb" %% "lift-json" % "2.5.1"

// Automatic serialization to JSON: https://github.com/scala/pickling

//Graph database https://github.com/twitter/flockdb

// ------------------ GUI -----------------

libraryDependencies += "jgoodies" % "forms" % "1.0.5"

//libraryDependencies += "com.jgoodies" % "jgoodies-common" % "1.4.0"

libraryDependencies <+= scalaVersion { "org.scala-lang" % "scala-swing" % _ }

// allows interaction with terminal? -> does not work
libraryDependencies <+= scalaVersion { "org.scala-lang" % "jline" % _ }

// argument phrasing: https://github.com/Rogach/scallop

// command line options parsing: https://github.com/scopt/scopt

// ScalaInterpreterPane - a Way ti interact with KAnalysis: https://github.com/Sciss/ScalaInterpreterPane
libraryDependencies += "de.sciss" %% "scalainterpreterpane" % "1.4.+"

// JFreeChart for scala: https://github.com/wookietreiber/scala-chart
// wrapped in scala: https://github.com/wookietreiber/scala-chart
libraryDependencies += "com.github.wookietreiber" %% "scala-chart" % "latest.integration"
