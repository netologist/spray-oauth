package spray.oauth.adapters.inmemory.utils

import scala.util.Random

/**
 * Created by hasanozgan on 31/07/14.
 */
object Sequence {
  private val seq = new Random()

  def nextId: Long = seq.nextLong().abs
}
