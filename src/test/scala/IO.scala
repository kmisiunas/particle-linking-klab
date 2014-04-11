import klab.io.Save
import org.scalatest.FunSuite

/**
 * User: karolis@misiunas.com
 * Date: 16/07/2013
 * Time: 00:37
 */
class IO extends FunSuite{

  test("Save file test") {
    Save("A test string to be saved", "testDir/testFile.txt")
  }


}
