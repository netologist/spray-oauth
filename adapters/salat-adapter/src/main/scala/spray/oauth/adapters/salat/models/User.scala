package spray.oauth.adapters.salat.models

import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import com.github.nscala_time.time.Imports._
import spray.oauth.adapters.salat.utils._
import spray.oauth.utils.HashUtils
import sun.security.provider.MD5
import java.security.MessageDigest

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 3/7/14
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */
case class User(@Key("_id") id: ObjectId,
  user_id: String,
  username: String,
  password: String,
  created_on: DateTime = DateTime.now,
  deleted_on: Option[DateTime],
  deleted: Boolean = false)

object UserDAO extends BaseDAO[User]("users") {

  def findWithCredentials(username: String, password: String) = {
    findOne(MongoDBObject("username" -> username, "password" -> HashUtils.md5(password)))
  }

  def create(user_id: String,
    username: String,
    password: String) = {

    this.insert(
      User(id = new ObjectId,
        user_id = user_id,
        username = username,
        password = HashUtils.md5(password),
        created_on = DateTime.now,
        deleted_on = None,
        deleted = false)
    )
  }

}