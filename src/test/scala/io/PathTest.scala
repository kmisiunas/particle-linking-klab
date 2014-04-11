package io

import org.scalatest.FunSuite
import klab.io.{Path, Save}

/**
 * User: karolis@misiunas.com
 * Date: 18/10/2013
 * Time: 01:24
 */
class PathTest extends FunSuite{

  val p1 = Path("/test/directory_01/file02.txt")
  val p2 = Path("/test/directory_02/m/g/file03.m")



  test("Path.name") {
    assert(p1.name == "file02.txt")
    assert(p2.name == "file03.m")
  }


}
