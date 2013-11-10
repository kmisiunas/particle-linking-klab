

name := "KLab"

//organization := "com.misiunas"

version := "0.1.4"

scalaVersion := "2.10.3"

// -------- Assembly Config ---------
// Use: assembly
// Use: assembly-package-dependency
// Use: assemblyPackageDependency

//test in assembly := {} // ignore tests



// ----------- Libraries ---------------

// GeoScala
libraryDependencies += "com.misiunas" %% "geoscala" % "0.1.1"

// For Breeze
libraryDependencies  += "com.github.fommil.netlib" % "all" % "1.1.1" pomOnly()

// BREEZE - https://github.com/scalanlp/breeze/
libraryDependencies  += "org.scalanlp" % "breeze_2.10" % "0.5"

resolvers ++= Seq(
            // other resolvers here
            // if you want to use snapshot builds (currently 0.5-SNAPSHOT), use this.
            "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
            "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

// TEST
libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"

// JUNIT
libraryDependencies += "junit" % "junit" % "4.10" % "test"

// Joda time for scala
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "0.6.0"

// Library for monitoring performance
//libraryDependencies += "nl.grons" %% "metrics-scala" % "3.0.0"

// Json: https://github.com/lift/lift/tree/master/framework/lift-base/lift-json
libraryDependencies += "net.liftweb" %% "lift-json" % "2.5.1"

// ------------------ GUI -----------------

libraryDependencies += "jgoodies" % "forms" % "1.0.5"

//libraryDependencies += "com.jgoodies" % "jgoodies-common" % "1.4.0"

// Remove latter, use Java swing for GUI
//libraryDependencies <+= scalaVersion { "org.scala-lang" % "scala-swing" % _ }

// allows interaction with terminal? -> does not work
libraryDependencies <+= scalaVersion { "org.scala-lang" % "jline" % _ }

// ScalaInterpreterPane - a Way ti interact with KAnalysis: https://github.com/Sciss/ScalaInterpreterPane
// libraryDependencies += "de.sciss" %% "scalainterpreterpane" % "1.4.+"

// JFreeChart for scala: https://github.com/wookietreiber/scala-chart
// wrapped in scala: https://github.com/wookietreiber/scala-chart
libraryDependencies += "com.github.wookietreiber" %% "scala-chart" % "latest.integration"
