package com.misiunas.kanalysis.gui.show

import processing.core.PApplet
import processing.core._
import com.misiunas.kanalysis.track.ParticleTrack
import com.alee.laf.slider.WebSlider
import javax.swing.{JFrame, SwingConstants}
import scala.swing.{Orientation, BoxPanel, MainFrame, Frame}

/**
 * User: karolis@misiunas.com
 * Date: 21/07/2013
 * Time: 21:12
 */
object ShowParticleTrack {

  def show(pt: ParticleTrack) = {
    //new DisplayFrame().setVisible(true)
    val frame = new ShowParticleTrackFrame
    val image = new TrackIllustration
    //frame.addProcessing(image)
    frame.show()
  }

  class DisplayFrame extends JFrame  {
    this.setSize(600, 600); //The window Dimensions
    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    val panel = new javax.swing.JPanel();
    panel.setBounds(20, 20, 600, 600);
    val sketch : processing.core.PApplet = new TrackIllustration;
    panel.add(sketch);

    val slider1 = new WebSlider( SwingConstants.HORIZONTAL );
    slider1.setMinimum ( 0 );
    slider1.setMaximum ( 100 );
    slider1.setMinorTickSpacing ( 10 );
    slider1.setMajorTickSpacing ( 50 );
    slider1.setPaintTicks ( false );
    slider1.setPaintLabels ( false );

    this.add(panel);
    this.add(slider1);
    sketch.init(); //this is the function used to start the execution of the sketch
    this.setVisible(true);
  }



  class TrackIllustration extends PApplet {

    override def setup = {
      size(400, 400);
      background(0);
    }
    override def draw = {
      background(0);
      fill(200);
      ellipseMode(PConstants.CENTER);
      ellipse(mouseX,mouseY,40,40);
    }
  }

}
