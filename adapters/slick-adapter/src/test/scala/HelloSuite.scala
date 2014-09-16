/**
 * Created by hasanozgan on 16/09/14.
 */
import org.scalatest.FunSuite

import scala.collection.mutable.Stack

class HelloSuite extends FunSuite {

  test("the name is set correctly in constructor") {
    val stack = new Stack[Int]
    stack.push(1)
    stack.push(2)
    assert(stack.pop() == 2)
    assert(stack.pop() == 1)
  }
}
