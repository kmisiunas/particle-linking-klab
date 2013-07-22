import com.misiunas.kanalysis.track.assemblies.{TrackAssembly}
import org.scalatest.FunSuite

/**
 * User: karolis@misiunas.com
 * Date: 16/07/2013
 * Time: 13:32
 */
class TrackAssemblyTest extends FunSuite {

  def approx(d1 :Double, d2:Double, acc: Double = 0.0001) : Boolean = (Math.abs(d1-d2)<acc)


  test("ParticleAssembly: to JSON and back") {
//    val pt1 = new ParticleTrack(5)
//    pt1.positions = List( List(0,1.1,0.1,0),
//      List(1,1.5,0.3,0),
//      List(2,0.9,-0.3,0),
//      List(3,2,-0.3,0),
//      List(4,1.7,0.1,0),
//      List(5,2.2,-0.1,0))
//
//    val pt2 = new ParticleTrack(4)
//    pt2.positions = List( List(5,4,3,2) )
//
//    val ta = new TrackAssembly
//    ta.list.append(pt1)
//    ta.list.append(pt2)
//    val enc = ta.toJSON
//    println("Test toJSON() for ParticleAssembly:"); println(enc)
    //decript
    //val ta2 = TrackAssembly.constructJSON(enc)
    //println(pt2.toJSON)
    //assert(ta == ta2)
  }

}