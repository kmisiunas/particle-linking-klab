package klab.image

import org.bytedeco.javacv.CanvasFrame
import org.bytedeco.javacpp.opencv_highgui._

/**
 * == see is openCV works ==
 *
 * Created by kmisiunas on 24/05/2014.
 */
object OpenCVTest {


  def run(file: String): Unit = {

    // Read an image.
    val image = cvLoadImage(file)

    // Create image window named "My Image."
    //
    // Note that you need to indicate to CanvasFrame not to apply gamma correction,
    // by setting gamma to 1, otherwise the image will not look correct.
    val canvas = new CanvasFrame("My Image")

    // Request closing of the application when the image window is closed.
    //canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)

    // Show image on window.
    canvas.showImage(image)
  }
}
