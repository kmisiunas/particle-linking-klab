package com.misiunas.kanalysis.gui.repl
import de.sciss.scalainterpreter._
import com.misiunas.kanalysis


/**
 * == A very simple version of REPL ==
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
    codeCfg.font    = Seq("Courier" -> 15)

    val intpCfg = Interpreter.Config()
    intpCfg.imports :+= "com.misiunas.kanalysis.track._"             // add default imports
    intpCfg.imports :+= "com.misiunas.kanalysis.track.assemblies._"             // add default imports
    intpCfg.imports :+= "com.misiunas.kanalysis.io.SaveFile.save"             // add default imports
    intpCfg.imports :+= "com.misiunas.kanalysis.io.LoadFile.loadString"             // add default imports
    intpCfg.imports :+= "com.misiunas.kanalysis.io.fileChooser"
    //intpCfg.bindings :+= NamedParam("pi", math.Pi) //

    val split = SplitPane(interpreterConfig = intpCfg,codePaneConfig = codeCfg)
    val f     = new javax.swing.JFrame(kanalysis.appName + "  (v"+kanalysis.appVersion+")")
    f.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    f.getContentPane.add(split.component, "Center")
    f.pack
    f.setVisible(true)
  }
}
