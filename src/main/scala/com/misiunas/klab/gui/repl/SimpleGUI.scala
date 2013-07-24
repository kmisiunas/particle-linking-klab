package com.misiunas.klab.gui.repl
import de.sciss.scalainterpreter._
import com.misiunas.klab
import com.misiunas.klab.gui.Imports


/**
 * == A very simple version of REPL ==
 *
 * User: karolis@misiunas.com
 * Date: 18/07/2013
 * Time: 17:43
 */
object SimpleGUI {

  def run = {
    val codeCfg     = CodePane.Config()          // creates a new configuration _builder_
    codeCfg.style   = Style.BlueForest               // use a light color scheme
    codeCfg.text    = "//\n" +
                      "//         \\,,,/\n" +
                      "//         (o o)\n" +
                      "//-----oOOo-(_)-oOOo-----\n"       // initial text to show in the widget

    codeCfg.text +=  """// default testing script
import com.misiunas.klab.track.corrections._
import com.misiunas.klab.track.geometry._
import com.misiunas.klab.gui.show._
val f = fileChooser
val ta = TrackAssembly(loadString(f))
ShowParticleTrack.show(ta(133))
val box = Box2D(Point(5,-100), 90, 200)
val ta2 = Continuum.autoCorrection(ta, box)
val nct = Continuum.findNonContinuousTracks(ta2, box)"""

    val intpCfg = Interpreter.Config()
    Imports.main.foreach( intpCfg.imports :+= _) // adds imports

    val split = SplitPane(interpreterConfig = intpCfg,codePaneConfig = codeCfg)
    val f     = new javax.swing.JFrame(klab.appName + "  (v"+klab.appVersion+")")
    f.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    f.getContentPane.add(split.component, "Center")
    f.pack
    f.setVisible(true)
  }
}
