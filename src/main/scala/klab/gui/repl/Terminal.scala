package klab.gui.repl

import scala.tools.nsc.interpreter.ILoop
import klab.gui.{ScriptEngine, Imports}
import klab.Main


/**
 * == GUI based on terminal ==
 *
 * User: karolis@misiunas.com
 * Date: 18/07/2013
 * Time: 19:51
 */
object Terminal {

  lazy val c = if(System.getProperty("os.name").toLowerCase.contains("mac")) new Colors() else new Colors()

  def apply() = {
    // not necessary?
//    lazy val urls = java.lang.Thread.currentThread.getContextClassLoader match {
//      case cl: java.net.URLClassLoader => cl.getURLs.toList
//      case _ => sys.error("classloader is not a URLClassLoader")
//    }
//    lazy val classpath = urls map {_.toString}

    // essential!
    val settings = new scala.tools.nsc.Settings
    settings.usejavacp.value = true // outside sbt
    //settings.embeddedDefaults[Main.type] // inside sbt
    settings.deprecation.value = true
    settings.withErrorFn(m => println(c.error + m + c.end)) // not sure what it does!

    //settings.embeddedDefaults[SampleILoop] // experimental support for running within sbt:http://www.scala-sbt.org/release/docs/faq
    // experimental auto detection of class paths : http://speaking-my-language.blogspot.co.uk/2009/11/embedded-scala-interpreter.html
    //settings.classpath.append("/Users/kmisiunas/Dropbox/PhD/Software/KAnalysis/target/scala-2.10/KAnalysis-assembly-0.1.1.jar")

    new Terminal().process(settings)
  }

}

class Terminal extends ILoop {

  def c = Terminal.c // quick access

  override def prompt = c.prompt +"==> "+c.end // custom prompt

  lazy val scriptEngine = new ScriptEngine(this)  // to exxecute automated scripts

  /** special printing function that shows prompt */
  def commandSc(sc:String) = {
    println(c.autoPrompt +"script> "+c.end + sc)
    command(sc)
  }

  addThunk {
   intp.beQuietDuring {
     Imports.main.foreach(intp.addImports(_))
     intp.bind("scriptEngine", scriptEngine) // enable scripts to be run as a commands: Run("file")
     command("def Run(script:String = \"\") = scriptEngine.run(script)")
    }
  }

  override def printWelcome() {
    echo(
      "\n" +
      "Welcome to " + klab.appName + " app (v"+klab.appVersion+")\n"+
      "This is a Terminal version based on Scala. \n"+
      "Type :help for help and :quit to end the pain. \n"+
      "\n\n" +
      "         \\,,,/\n" +
      "         (o o)\n" +
      "-----oOOo-(_)-oOOo-----")
  }

  /** special command input for surpressing result printing if command ends with ; */
  override def command(line: String): Result = {
    if(line.replaceAll("//.+$", "").trim.takeRight(1) == ";") intp.beSilentDuring(super.command(line))
    else super.command(line)
  }


// use the code below if you want syntax highlighting

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
