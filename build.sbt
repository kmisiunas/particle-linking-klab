
name := "KLab"

version := "0.1.5"

scalaVersion := "2.10.3"

// -------- Assembly Config ---------
// Use: assembly
// Use: assembly-package-dependency
// Use: assemblyPackageDependency


// ----------- Libraries ---------------

// ----------- Main ----------------

// GeoScala
libraryDependencies += "com.misiunas" %% "geoscala" % "0.1.2"

// For Breeze
// libraryDependencies  += "com.github.fommil.netlib" % "all" % "1.1.1" pomOnly()

// BREEZE - https://github.com/scalanlp/breeze/
libraryDependencies  += "org.scalanlp" %% "breeze" % "0.5.2"

resolvers ++= Seq(
  // other resolvers here
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

// TEST - http://www.scalatest.org/
libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

// Joda time for scala - https://github.com/nscala-time/nscala-time
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "0.6.0"

// Json: https://github.com/lift/lift/tree/master/framework/lift-base/lift-json
libraryDependencies += "net.liftweb" %% "lift-json" % "2.5.1"

// ------------------ GUI -----------------

// library for GUI designer in Intellij
libraryDependencies += "com.intellij" % "javac2" % "7.0.3"

// allows interaction with terminal? -> does not work
//libraryDependencies <+= scalaVersion { "org.scala-lang" % "jline" % _ }
libraryDependencies += "org.scala-lang" % "jline" % "2.10.3"

// Adds compiler that is needed for REPL
libraryDependencies +=  "org.scala-lang" % "scala-compiler" % "2.10.3"

// JFreeChart for scala: https://github.com/wookietreiber/scala-chart
// wrapped in scala: https://github.com/wookietreiber/scala-chart
libraryDependencies += "com.github.wookietreiber" %% "scala-chart" % "latest.integration"


// ------------- Unnessasary ---------------

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