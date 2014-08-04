package spray.oauth.adapters.salat.utils

import com.typesafe.config.ConfigFactory
import com.mongodb.casbah.{ MongoDB, MongoURI, MongoCollection, MongoConnection }

object MongoFactory {

  lazy val conf = ConfigFactory.load()

  def getCollection(collection: String): MongoCollection = {
    lazy val uri = MongoURI(conf.getString(s"spray.oauth2.datasource.uri"))
    lazy val connection = MongoConnection(uri)(uri.database.get)(collection)
    connection
  }
}