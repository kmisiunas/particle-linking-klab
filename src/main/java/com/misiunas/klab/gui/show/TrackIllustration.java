package com.misiunas.klab.gui.show;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * User: karolis@misiunas.com
 * Date: 22/07/2013
 * Time: 02:38
 */
class TrackIllustration extends PApplet {

    public void setup()  {
        size(400, 400);
        background(0);
    }
    public void draw()  {
        background(0);
        fill(200);
        ellipseMode(PConstants.CENTER);
        ellipse(mouseX,mouseY,40,40);
    }
}
