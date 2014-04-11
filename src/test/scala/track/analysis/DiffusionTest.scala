package track.analysis

import org.scalatest.FunSuite
import klab.track.Track
import klab.track.geometry.position.Pos
import klab.track.assemblies.TrackAssembly
import klab.track.analysis.{Diffusion, Find}
import com.misiunas.geoscala.vectors.Vec
import klab.track.operators.TimeOperator

/**
 * Created with IntelliJ IDEA.
 * User: kmisiunas
 * Date: 04/11/2013
 * Time: 11:53
 * To change this template use File | Settings | File Templates.
 */
class DiffusionTest extends FunSuite {

  val pt1 = Track(1, List( Pos(1,2,1), Pos(2,0,0), Pos(3,1,1), Pos(5,0,0),   Pos(6,1,1) )).qualityCheck
  val pt2 = Track(2, List( Pos(1,2,-3), Pos(2,0,0), Pos(3,1,1), Pos(4,0,0),   Pos(5,1,1), Pos(6,0,0),
                                    Pos(7,1,1), Pos(8,2,2),   Pos(9,3,3))).qualityCheck

  test("Diffusion.msd(1) #1") {
    val t = Diffusion.msd(1)(pt1.list)
    assert(t.size == 1 && t.head.size == 1, t)
    assert(t.head.head.n == 2)
    assert(t.head.head.msd == Vec(4,1,0))
    assert(t.head.head.pos == Pos(1.5,1,0.5,0))
  }

  test("Diffusion.msd(1) #2") {
    val t = Diffusion.msd(1)(pt2.list)
    assert(t.size == pt2.size-1 && t.head.size == 1)
    assert(t(3).head.n == 2)
    assert(t(3).head.msd == Vec(1,1,0))
    assert(t(3).head.pos == Pos(4.5,0.5,0.5,0), t.map(_.head.pos))
  }

  test("Diffusion.msd(2) ") {
    assert(Diffusion.msd(2)(pt1.list) == Nil) // empty

    val t = Diffusion.msd(2)(pt2.list)
    assert(t.size == pt2.size-2 && t.head.size == 2,
      "t.size="+t.size +", pt2.size="+pt2.size +"\n size list=" + t.map(_.size))
    assert(t(0)(1).n == 3)
    assert(t(0)(0).msd == Vec(4,9,0))
    assert(t(0)(1).msd == Vec(2.5,12.5,0), "msd = " + t(0)(1).msd +"\n pos= "+ t(0)(1).pos)
    assert(t(0)(1).pos == Pos(2,3/3,-2.0/3,0))
  }


  test("Diffusion.naive_Di") {
    val di = Diffusion.naive_Di(pt2.list)
    assert(di(0).Di == Vec(2, 4.5, 0))
    assert(di(1).Di == Vec(0.5, 0.5, 0))
    assert(di(0).pos == Pos(1.5, 1, -1.5))
    assert(di(1).pos == Pos(2.5, 0.5, 0.5))
  }

  test("Diffusion.SavingAndDoyle_Di") {
    val di = Diffusion.savingAndDoyle_Di(pt2.list)
    assert(di(0).Di == (Vec(2.5,12.5,0) - Vec(4,9,0)) * 0.5)
    assert(di(0).pos == Pos(2, 1, -2.0/3))
  }


  test("Diffusion.unitsOfDi ") {
    assert( Diffusion.unitsOfDi(pt2) == List("px_x^2 / frame", "px_y^2 / frame", "px_z^2 / frame") , Diffusion.unitsOfDi(pt2))
  }






}
