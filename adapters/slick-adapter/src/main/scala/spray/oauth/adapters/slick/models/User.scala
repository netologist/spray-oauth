package spray.oauth.adapters.slick.models

import org.joda.time.DateTime
import spray.oauth.adapters.slick.utils.BaseDAO

import scala.slick.driver.JdbcDriver.simple._


/**
 * Created by hasanozgan on 14/09/14.
 */
class User(tag:Tag) extends Table[(Int, String, String, String, DateTime, DateTime, Boolean)](tag, "USERS") {
  def id = column[Int]("ID", O.PrimaryKey) // This is the primary key column
  def user_id = column[String]("USER_ID")
  def username = column[String]("USERNAME")
  def password = column[String]("PASSWORD")
  def created_on = column[DateTime]("CREATED_ON")
  def deleted_on = column[DateTime]("DELETED_ON")
  def deleted = column[Boolean]("DELETED")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, user_id, username, password, created_on, deleted_on, deleted)
}

object UserDAO extends BaseDAO[User] {

  val querySalesByName = for {
    name <- Parameters[String]
    c <- tableQuery if c.username is name
  } yield c.password

}



