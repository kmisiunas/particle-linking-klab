package track.operators

import org.scalatest.FunSuite
import klab.io.Save
import klab.track.operators.Bin

/**
 * Created by kmisiunas on 09/12/2013.
 */
class BinTest extends FunSuite{

  test("Bin()") {
    val bins = breeze.linalg.linspace(0, 4, 5)
    val testValues: List[(Double,Double)] = List(
      (0,1.5), (4,1.2), (3, 1.3), (1.1, 2.1), (6,3.1), (0.49,1)
    )
    assert( Bin(bins, testValues).toArray.toList == List(2.5, 2.1, 0, 1.3, 1.2) )
  }


}
