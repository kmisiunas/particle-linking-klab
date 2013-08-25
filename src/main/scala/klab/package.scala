package com.misiunas

import com.misiunas.klab.gui.repl.{Terminal, SimpleGUI}
import com.alee.laf.WebLookAndFeel
import com.misiunas.klab.gui.show.ShowParticleTrack
import javax.swing.{JFrame, UIManager}
import com.misiunas.klab.track.ParticleTrack
import com.misiunas.klab.track.geometry.position.Pos
import java.awt.Component

/**
 * User: karolis@misiunas.com
 * Date: 12/07/2013
 * Time: 15:39
 */
package object klab {

  val appName = "K-Lab"

  val appVersion = "0.1.3"

  UIManager.setLookAndFeel ( new WebLookAndFeel() ); // Swing L&F

  def main(args: Array[String]) {

    if(args.isEmpty) SimpleGUI.run
    else
      args.head match {
        case "terminal" | "t" | "console" | "-t" => Terminal()
        case _ => SimpleGUI.run
      }
  }

  lazy val guiOwner: Component = {
    val frame = new JFrame(appName)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.getContentPane
  }
}
