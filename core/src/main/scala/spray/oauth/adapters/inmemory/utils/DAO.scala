package spray.oauth.adapters.inmemory.utils

import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap

/**
 * Created by hasanozgan on 31/07/14.
 */
class DAO[T <: Entity] {
  private val items = new HashSet[T]

  def insert(entity: T) = {
    items.add(entity)
  }

  def save(entity: T) = {
    items.add(entity)
  }

  def remove(entity: T) = {
    items.remove(entity)
  }

  def get(id: Long) = {
    items.find(p => p.getId == id)
  }

  def findBy(p: (T) => Boolean) = {
    items.find(p)
  }

}
