package com.misiunas

import com.misiunas.kanalysis.gui.repl.{Terminal, SimpleGUI}
import com.alee.laf.WebLookAndFeel
import com.misiunas.kanalysis.gui.show.ShowParticleTrack
import javax.swing.UIManager

/**
 * User: karolis@misiunas.com
 * Date: 12/07/2013
 * Time: 15:39
 */
package object kanalysis {

  val appName = "KAanlysis"

  val appVersion = "0.1.1"

  UIManager.setLookAndFeel ( new WebLookAndFeel () ); // Swing L&F

  def main(args: Array[String]) {

    //UIManager.setLookAndFeel ( new WebLookAndFeel () ); // Swing L&F

    ShowParticleTrack.show(null)

    if(args.isEmpty) SimpleGUI.run
    else
      args.head match {
        case "terminal" | "t" | "console" | "-t" => Terminal
        case _ => SimpleGUI.run
      }
  }
}
