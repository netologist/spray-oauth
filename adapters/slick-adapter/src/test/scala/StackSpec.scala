import spray.oauth.adapters.slick.models.UserDAO

import collection.mutable.Stack
import org.scalatest._

/**
 * Created by hasanozgan on 16/09/14.
 */
class StackSpec extends FlatSpec with Matchers {

  "A Stack" should "pop values in last-in-first-out order" in {
    val stack = new Stack[Int]
    stack.push(1)
    stack.push(2)
    stack.pop() should be(2)
    stack.pop() should be(1)

    UserDAO.initial
  }

  it should "throw NoSuchElementException if an empty stack is popped" in {
    val emptyStack = new Stack[Int]
    a[NoSuchElementException] should be thrownBy {
      emptyStack.pop()
    }
  }
}