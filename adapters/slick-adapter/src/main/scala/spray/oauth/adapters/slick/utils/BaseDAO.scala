package spray.oauth.adapters.slick.utils

import com.typesafe.config.ConfigFactory

import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.lifted.{ AbstractTable, TableQuery }

/**
 * Created by hasanozgan on 04/08/14.
 */
class BaseDAO {
  lazy val conf = ConfigFactory.load()
  lazy val uri = conf.getString(s"spray.oauth2.datasource.uri")
  lazy val user = conf.getString(s"spray.oauth2.datasource.username")
  lazy val password = conf.getString(s"spray.oauth2.datasource.password")
  lazy val driver = conf.getString(s"spray.oauth2.datasource.driver")

  protected val defaultDB = Database.forURL(uri, user, password, driver = driver)
}
