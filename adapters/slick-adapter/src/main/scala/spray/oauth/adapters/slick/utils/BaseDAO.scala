package spray.oauth.adapters.slick.utils

import scala.slick.jdbc.JdbcBackend.Database

/**
 * Created by hasanozgan on 04/08/14.
 */
class BaseDAO {
  protected val defaultDB = Database.forURL(url, driver = driver, user=user, prop=props, password=password)

}
