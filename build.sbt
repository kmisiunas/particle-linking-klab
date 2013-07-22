import AssemblyKeys._ // put this at the top of the file

assemblySettings

name := "KAnalysis"

version := "0.1.1"

scalaVersion := "2.10.2"

resourceDirectory <<= baseDirectory { _ / "res" }

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

// ------------------ GUI -----------------

libraryDependencies <+= scalaVersion { "org.scala-lang" % "scala-swing" % _ }

// allows interaction with terminal? -> does not work
//libraryDependencies <+= scalaVersion { "org.scala-lang" % "jline" % _ }

// argument phrasing: https://github.com/Rogach/scallop

// command line options parsing: https://github.com/scopt/scopt

// ScalaInterpreterPane - a Way ti interact with KAnalysis: https://github.com/Sciss/ScalaInterpreterPane
libraryDependencies += "de.sciss" %% "scalainterpreterpane" % "1.4.+"

// JFreeChart for scala: https://github.com/wookietreiber/scala-chart
