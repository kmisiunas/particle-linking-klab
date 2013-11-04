package io

import org.scalatest.FunSuite
import klab.io.Path
import klab.io.infrastructure.load.ImportTrackAssembly

/**
 * User: karolis@misiunas.com
 * Date: 18/10/2013
 * Time: 10:50
 */
class ImportTrackAssemblyTest extends FunSuite{

  val p1 = Path("/test/directory_01/BA0.txt")
  val p2 = Path("/test/directory_02/m/g/file03.m")
  val p3 = Path("/test/directory_02/m/g/filter.txt")
  val p4 = Path("/test/directory_02/m/g/this is a comment file.txt")


  test("filterTitlesWith") {
    val sel = List( p1,p2,p3,p4 ).filter( f => ImportTrackAssembly.filterTitlesWith.forall( !f.name.contains(_) ) )
    assert(sel == List(p1,p2))
  }

}
