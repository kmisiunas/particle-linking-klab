package klab

import javax.swing.{JFrame, JOptionPane, UIManager}
import com.alee.laf.WebLookAndFeel
import klab.gui.repl.Terminal
import klab.io.Path
import java.awt.datatransfer.{Clipboard, StringSelection}
import java.awt.{Image, Component, Toolkit}
import com.apple.eawt.Application

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
object KLab {

  val appName = "KLab"

  val appVersion = "0.1.7"

  lazy val guiOwner: Component = {
    val frame = new JFrame(appName)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.getContentPane
  }



  /** Main object */
  def main(args: Array[String]) {

    UIManager.setLookAndFeel ( new WebLookAndFeel() ); // Swing L&F

    if (util.Properties.isMac) runTerminalWithDecorations() // to be runnable with pack
    else if(args.isEmpty){
      //Terminal()

      val file: String = Path(KLab.getClass.getProtectionDomain().getCodeSource().getLocation().getPath())
      val command: String = "java -Xms1g -Xmx2g -jar " + file + " -t"

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
        case "terminal" | "t" | "console" | "-t" => runTerminalWithDecorations()
        case _ => println("Unrecognised statement")
      }


    def runTerminalWithDecorations(): Unit = {
      // Custom terminal for Mac OS X
      // Get image
      val application: Application = Application.getApplication()
      val image: Image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png"))
      application.setDockIconImage(image)
      // change name - does not work!
      System.setProperty("apple.laf.useScreenMenuBar", "true")
      //System.setProperty("com.apple.mrj.application.apple.menu.about.name", "KLab in Background")
      // Start terminal
      Terminal()
    }

  }

}
