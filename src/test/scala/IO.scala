import com.misiunas.kanalysis.io.SaveFile._
import org.scalatest.FunSuite

/**
 * User: karolis@misiunas.com
 * Date: 16/07/2013
 * Time: 00:37
 */
class IO extends FunSuite{

  test("Save file test") {
    save("A test string to be saved", "testDir/testFile.txt")
  }


}
