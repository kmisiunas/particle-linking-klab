package klab

import javax.swing.{JOptionPane, UIManager}
import com.alee.laf.WebLookAndFeel
import klab.gui.repl.Terminal
import klab.io.Path
import com.alee.laf.optionpane.WebOptionPane
import java.awt.datatransfer.{Clipboard, StringSelection}
import java.awt.Toolkit

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

      val file: String = Path(Main.getClass.getProtectionDomain().getCodeSource().getLocation().getPath())
      val command: String = "java -jar " + file + " -t"

      val options: Array[AnyRef] = Array("Copy to clipboard", "Cancel")
      val res = JOptionPane.showOptionDialog ( null, "Run command in Terminal or PowerShell: \n" + command,
        "KLab Instructions",JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options(0) )
      // copy command to clipboard
      if (options(res) == "Copy to clipboard") {
        val  stringSelection: StringSelection = new StringSelection (command);
        val clpbrd: Clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
        clpbrd.setContents (stringSelection, null);
      }
      println("To run Klab run this: " )
      println(command)

    }
    else
      args.head match {
        case "terminal" | "t" | "console" | "-t" => Terminal()
        case _ => println("Unrecognised statement")
      }
  }

}
