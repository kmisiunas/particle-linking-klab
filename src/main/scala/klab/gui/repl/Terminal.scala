package klab.gui.repl

import scala.tools.nsc.interpreter.{ReplReporter, ILoop}
import klab.gui.{Print, Imports}

/**
 * == GUI based on terminal ==
 *
 * Main interaction interface based on Scala REPL
 *
 * Aims:
 *  - [x] Fully Scala based and compatible
 *  - [x] Coloring for better support
 *  - [x] Suppress output if input has semi colon ;
 *  - [ ] Custom error messages with ability to get full message
 *  - [x] Unix and Windows support
 *
 *
 * Version 0.1.6
 * User: karolis@misiunas.com
 * Date: 18/07/2013
 */
object Terminal {

  def apply() = {
    // essential!
    val settings = new scala.tools.nsc.Settings
    settings.usejavacp.value = true // outside sbt
    //settings.embeddedDefaults[Main.type] // inside sbt - not wery useful
    settings.deprecation.value = true
    settings.withErrorFn(m => println(Colors.error + m + Colors.end)) // not sure what it does!

    new Terminal().process(settings)
  }

}

class Terminal extends ILoop {

  override def prompt = Colors.prompt + "==> " + Colors.end // custom prompt

  lazy val scriptEngine = new ScriptEngine(this)  // to exxecute automated scripts

  addThunk {
   intp.beQuietDuring {
     Imports.main.foreach(intp.addImports(_))
     intp.bind("scriptEngine", scriptEngine) // enable scripts to be run as a commands: Run("file")
     command("def Run(script:String = \"\") = scriptEngine.run(script)")
     intp.bind("debug", this) // for debugging Terminal structure
    }
    Print.printMethod = st => {intp.reporter.printMessage(st); intp.reporter.flush() }
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

  /** special command input for suppressing result printing if command ends with ; */
  override def command(line: String): Result = {
    if(line.replaceAll("//.+$", "").trim.takeRight(1) == ";") intp.beSilentDuring(super.command(line))
    else super.command(line)
  }

  /** Create a new interpreter. */
  override def createInterpreter() {
    if (addedClasspath != "")
      settings.classpath append addedClasspath
    intp = new KLabInterpreter
  }

  class KLabInterpreter extends ILoopInterpreter {
    override lazy val reporter: ReplReporter = new KLabReporter(this)
  }

  // not very important - help for the user
  override def commands: List[LoopCommand] = super.commands ++
    List(LoopCommand.nullary("error", "show the suppressed java error/Exception", errorCommand))


  /** executes with :error */
  private def errorCommand(): Result = {
    KLabReporter.lastError match {
      case None => "Can't find any cached errors."
      case Some(err) => "Full error message: \n" + err
    }
  }


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
}
