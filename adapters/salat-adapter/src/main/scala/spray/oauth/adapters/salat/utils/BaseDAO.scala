package spray.oauth.adapters.salat.utils

import com.novus.salat.dao.SalatDAO
import com.mongodb.casbah.Imports._
import com.novus.salat.Context

/**
 * Created by hasanozgan on 04/03/14.
 */
class BaseDAO[T <: AnyRef](val coll: String)(implicit mot: Manifest[T], mid: Manifest[ObjectId], ctx: Context)
    extends SalatDAO[T, ObjectId](MongoFactory.getCollection(coll)) {

  collection.setWriteConcern(wc)

  def findAll: List[T] = {
    find(MongoDBObject.empty).toList
  }

  def removeAll = {
    collection.remove(MongoDBObject.empty)
  }

  def drop() {
    collection.drop()
  }
  /*
  def save(bulk: List[T], wc: WriteConcern = WriteConcern.Safe) {
    bulk.foreach {
      super.save(_, wc)
    }
  }
*/
  def findById(id: String): Option[T] = {
    findOneById(new ObjectId(id))
  }

}
