package com.misiunas.klab

import java.awt.FileDialog
import com.misiunas.klab

/**
 * User: karolis@misiunas.com
 * Date: 18/07/2013
 * Time: 21:09
 */
package object io {

  /** file picker - can be called form the code */
  def fileChooser() : String = {
    val f     = new javax.swing.JFrame(klab.appName + "  (v"+klab.appVersion+")")
    val fd: FileDialog  = new FileDialog(f, "Choose a file", FileDialog.LOAD);
    //fd.setDirectory("\"");
    //fd.setFile("*.xml");
    fd.setVisible(true);
    val filePath : String = fd.getDirectory + fd.getFile;
    fd.dispose()
    f.setVisible(false); //you can't see me!
    f.dispose(); //Destroy the JFrame object
    return filePath;
  }

}
