
import klab.gui.repl.{Terminal}
import com.alee.laf.WebLookAndFeel
import javax.swing.{JFrame, UIManager}
import java.awt.Component

/**
 * User: karolis@misiunas.com
 * Date: 12/07/2013
 * Time: 15:39
 */
package object klab {

  val appName = "K-Lab"

  val appVersion = "0.1.4"

  UIManager.setLookAndFeel ( new WebLookAndFeel() ); // Swing L&F

  def main(args: Array[String]) {



    if(args.isEmpty){
      //val p = Runtime.getRuntime().exec("java -cp \"KLab.jar:KLab-assembly-0.1.4-deps.jar:scala-library-2.10.3-assembly.jar\"  -t");
    }
    else
      args.head match {
        case "terminal" | "t" | "console" | "-t" => Terminal()
        case _ => println("Unrecognised statement")
      }
  }

  lazy val guiOwner: Component = {
    val frame = new JFrame(appName)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.getContentPane
  }
}
