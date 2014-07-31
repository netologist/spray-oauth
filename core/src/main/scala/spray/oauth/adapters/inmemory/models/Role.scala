package spray.oauth.adapters.inmemory.models

import com.github.nscala_time.time.Imports._
import spray.oauth.adapters.inmemory.utils.{ Entity, DAO }
import scala.collection.JavaConverters

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 3/7/14
 * Time: 10:44 AM
 * To change this template use File | Settings | File Templates.
 */
case class Role(id: Long,
    name: String,
    scopes: List[String],
    grants: List[String],
    created_on: DateTime = DateTime.now,
    deleted_on: Option[DateTime] = None,
    deleted: Boolean = false) extends Entity(id) {
}

object RoleDAO extends DAO[Role]

