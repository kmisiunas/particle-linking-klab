package operators

import org.scalatest.FunSuite
import klab.track.geometry.position.Pos
import klab.track.operators.TimeOperator

/**
 * Created by kmisiunas on 11/12/2013.
 */
class TimeOperatorTest extends FunSuite{

  test("isContinuous") {
    val l1 = List(Pos(1,1),Pos(2,2),Pos(3,1),Pos(4,1))
    assert( TimeOperator.isContinuous(l1) )
    val l2 = List(Pos(1,1),Pos(2,2),Pos(3,1),Pos(5,1))
    assert( !TimeOperator.isContinuous(l2) )
    val l3 = List(Pos(2,1),Pos(1,2),Pos(3,1),Pos(4,1))
    assert( !TimeOperator.isContinuous(l3) )
    val l4 = List(Pos(4,1))
    assert( TimeOperator.isContinuous(l4) )
  }

}
