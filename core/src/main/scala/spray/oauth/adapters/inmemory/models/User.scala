package spray.oauth.adapters.inmemory.models

import com.github.nscala_time.time.Imports._
import spray.oauth.adapters.inmemory.utils.{ Entity, DAO, Sequence }
import spray.oauth.utils.HashUtils

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 3/7/14
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */
case class User(id: Long,
    user_id: String,
    username: String,
    password: String,
    created_on: DateTime = DateTime.now,
    deleted_on: Option[DateTime],
    deleted: Boolean = false) extends Entity(id) {
}

object UserDAO extends DAO[User] {

  def findWithCredentials(username: String, password: String) = {
    findBy(x => x.username.equals(username) && x.password.equals(HashUtils.md5(password)))
  }

  def create(user_id: String,
    username: String,
    password: String) = {

    save(
      User(id = Sequence.nextId,
        user_id = user_id,
        username = username,
        password = HashUtils.md5(password),
        created_on = DateTime.now,
        deleted_on = None,
        deleted = false)
    )
  }

}