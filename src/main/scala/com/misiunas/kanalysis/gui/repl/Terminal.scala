package com.misiunas.kanalysis.gui.repl

import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.{ReplReporter, ILoop}
import com.misiunas.kanalysis

import com.misiunas.kanalysis.io.LoadFile.loadString



/**
 * == GUI based on terminal ==
 *
 * User: karolis@misiunas.com
 * Date: 18/07/2013
 * Time: 19:51
 */
object Terminal {

  lazy val urls = java.lang.Thread.currentThread.getContextClassLoader match {
    case cl: java.net.URLClassLoader => cl.getURLs.toList
    case _ => sys.error("classloader is not a URLClassLoader")
  }
  lazy val classpath = urls map {_.toString}

  val settings = new Settings
  settings.usejavacp.value = true
  settings.deprecation.value = true
  println(settings.classpath.value)
  //classpath.foreach(settings.classpath.append(_))
  //classpath.foreach(println(_))
  settings.classpath.append("/Users/kmisiunas/Dropbox/PhD/Software/KAnalysis/target/scala-2.10/KAnalysis-assembly-0.1.1.jar")
  new SampleILoop().process(settings)

}

class SampleILoop extends ILoop {

  // custom comand request
  final val psColor = "\033[36m"  // http://www.scala-lang.org/api/current/index.html#scala.Console$
  lazy val promptSymbol : String =
    if(System.getProperty("os.name").toLowerCase.contains("mac")) psColor+"==> "+"\033[0m" else "==> "
  override def prompt = promptSymbol

  addThunk {
   intp.beQuietDuring {
     intp.addImports("java.lang.Math._")
      intp.addImports("com.misiunas.kanalysis.track._")
      intp.addImports("com.misiunas.kanalysis.track.assemblies._")
      intp.addImports("com.misiunas.kanalysis.io.SaveFile.save")
      intp.addImports("com.misiunas.kanalysis.io.LoadFile.loadString")
      intp.addImports("com.misiunas.kanalysis.io.fileChooser")
    }
  }

  override def printWelcome() {
    echo(
      "\n" +
      "Welcome to " + kanalysis.appName + " app (v"+kanalysis.appVersion+")\n"+
      "This is a Terminal version based on Scala. \n"+
      "Type :help for help and :quit to end the pain. \n"+
      "\n\n" +
      "         \\,,,/\n" +
      "         (o o)\n" +
      "-----oOOo-(_)-oOOo-----")
  }

//    var gremlinIntp: GremlinInterpreter = _
//    override def createInterpreter() {
//      if (addedClasspath != "")
//        settings.classpath.append(addedClasspath)
//      gremlinIntp = new GremlinInterpreter
//      intp = gremlinIntp
//    }

//    /**Overriden to print out the value evaluated from the specified line. */
//    override def command(line: String): Result = {
//      val result = super.command(line)
//
//      //TODO handle compiler error
//      //TODO handle exception
//      //TODO handle something like class on multilines
//      if (result.keepRunning && result.lineToRecord.isDefined)
//        printLastValue
//
//      result
//    }

//    /**Prints the last value by expanding its elements if it's iterator-like or collection-like. */
//    def printLastValue() = gremlinIntp.lastValue match {
//      case Right(value)    ⇒ for (v ← toIterator(value)) out.println("==>" + v)
//      case Left(throwable) ⇒ throwable.printStackTrace(out)
//    }
//
//    /**Coerces the specified value into an iterator. */
//    def toIterator(value: Any): Iterator[Any] = {
//      import scala.collection.JavaConverters._
//      value match {
//        case t: Traversable[Any]        ⇒ t.toIterator
//        case a: Array[_]                ⇒ a.toIterator
//        case i: java.lang.Iterable[Any] ⇒ i.asScala.toIterator
//        case i: java.util.Iterator[Any] ⇒ i.asScala
//        case m: java.util.Map[Any, Any] ⇒ m.asScala.toIterator
//        case _                          ⇒ Iterator.single(value)
//      }
//    }

//    class GremlinInterpreter extends ILoopInterpreter {
//      override lazy val reporter: ReplReporter = new ReplReporter(this) {
//        /**Stop ReplReporter from printing to console. Instead we print in GremlinILoop.command. */
//        override def printMessage(msg: String) {}
//      }
//      def prevRequest: Option[Request] = prevRequestList.lastOption
//
//      /**Returns the last value evaluated by this interpreter. See https://issues.scala-lang.org/browse/SI-4899 for details. */
//      def lastValue: Either[Throwable, AnyRef] =
//        prevRequest.getOrElse(throw new NullPointerException()).lineRep.callEither("$result")
//    }


}
