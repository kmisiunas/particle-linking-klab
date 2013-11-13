
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

  val appVersion = "0.1.5"

  lazy val guiOwner: Component = {
    val frame = new JFrame(appName)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.getContentPane
  }

}
