package klab

import javax.swing.{JFrame, UIManager}
import com.alee.laf.WebLookAndFeel
import klab.gui.repl.Terminal
import java.awt.Component

/**
 * Main class
 * ----------
 *
 * Entry point for KLab software.
 *
 * Created with IntelliJ IDEA.
 * User: kmisiunas
 * Date: 10/11/2013
 * Time: 00:39
 */
object Main {

  /** Main object */
  def main(args: Array[String]) {

    UIManager.setLookAndFeel ( new WebLookAndFeel() ); // Swing L&F

    if(args.isEmpty){
      //Terminal()
      println("Debug - Hello World!")
      //val p = Runtime.getRuntime().exec("java -cp \"KLab.jar:KLab-assembly-0.1.4-deps.jar:scala-library-2.10.3-assembly.jar\"  -t");
    }
    else
      args.head match {
        case "terminal" | "t" | "console" | "-t" => Terminal()
        case _ => println("Unrecognised statement")
      }
  }

}
