package spray.oauth.adapters.slick.models

import org.joda.time.DateTime
import spray.oauth.adapters.slick.utils.BaseDAO

import com.github.tototoshi.slick.JdbcJodaSupport._
import scala.slick.driver.JdbcDriver.simple._

/**
 * Created by hasanozgan on 14/09/14.
 */
case class User(id: Long, user_id: String, username: String, password: String, created_on: DateTime, deleted_on: DateTime, deleted: Boolean)

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[Long]("ID", O.PrimaryKey) // This is the primary key column
  def user_id = column[String]("USER_ID")
  def username = column[String]("USERNAME")
  def password = column[String]("PASSWORD")
  def created_on = column[DateTime]("CREATED_ON")
  def deleted_on = column[DateTime]("DELETED_ON")
  def deleted = column[Boolean]("DELETED")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, user_id, username, password, created_on, deleted_on, deleted) <> (User.tupled, User.unapply)
}

object UserDAO extends BaseDAO {

  val users = TableQuery[Users]

  def initial: Unit = {
    defaultDB withSession { implicit session =>
      users.ddl.create
      //users += (10, "123", "meddah", "123456", DateTime.now, DateTime.now, deleted = false)
    }
  }

  val querySalesByName = for {
    name <- Parameters[String]
    c <- users if c.username is name
  } yield c.password

}

