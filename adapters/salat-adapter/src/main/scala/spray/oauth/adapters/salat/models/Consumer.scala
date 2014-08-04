package spray.oauth.adapters.salat.models

import com.novus.salat.global._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import com.github.nscala_time.time.Imports._
import spray.oauth.utils.OAuth2Utils
import spray.oauth.adapters.salat.utils.BaseDAO

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 3/7/14
 * Time: 10:44 AM
 * To change this template use File | Settings | File Templates.
 */

case class Consumer(@Key("_id") id: ObjectId,
    @Key("_fk_role") fk_role: ObjectId,
    name: String,
    scopes: List[String] = Nil,
    grants: List[String] = Nil,
    site_url: Option[String],
    logo: Option[String],
    description: Option[String],
    callback_url: Option[String],
    client_secret: String,
    created_on: DateTime = DateTime.now,
    deleted_on: Option[DateTime] = None,
    deleted: Boolean = false) {
  def client_id = id.toString
}

object ConsumerDAO extends BaseDAO[Consumer]("consumers") {
  collection.ensureIndex("_idx_roles")

  private def mergedScopeList(consumer: Consumer, role: Role) = (consumer.scopes ::: role.scopes).distinct.toList
  private def mergedGrantList(consumer: Consumer, role: Role) = (consumer.grants ::: role.grants).distinct.toList
  private def getRole(consumer: Consumer, role: Option[Role]) = if (role.isEmpty) RoleDAO.findOneById(consumer.fk_role) else role

  def fetchScopeList(consumer: Consumer, role: Option[Role] = None) = {
    getRole(consumer, role) match {
      case Some(r) => mergedScopeList(consumer, r)
      case None => consumer.scopes
    }
  }

  def fetchGrantList(consumer: Consumer, role: Option[Role] = None) = {
    getRole(consumer, role) match {
      case Some(r) => mergedGrantList(consumer, r)
      case None => consumer.grants
    }
  }

  def findWithCredentials(client_id: String, client_secret: String): Option[Consumer] = {
    findOne(MongoDBObject("_id" -> new ObjectId(client_id), "client_secret" -> client_secret))
  }

  def findWithClientIdAndScope(client_id: String, scope: Option[String]): Option[Consumer] = {
    findById(client_id) match {
      case Some(consumer) => {
        val requestedScopes = OAuth2Utils.toScopeList(scope)
        requestedScopes diff ConsumerDAO.fetchScopeList(consumer) match {
          case Nil if requestedScopes.size > 0 => Some(consumer)
          case _ => None
        }
      }
      case None => None
    }
  }

  def findWithCredentialsWithScope(client_id: String, client_secret: String, scope: Option[String]): Option[Consumer] = {
    ConsumerDAO.findWithCredentials(client_id, client_secret) match {
      case Some(consumer) => {
        val requestedScopes = OAuth2Utils.toScopeList(scope)
        requestedScopes diff ConsumerDAO.fetchScopeList(consumer) match {
          case Nil if requestedScopes.size > 0 => Some(consumer)
          case _ => None
        }
      }
      case None => None
    }
  }

  def findConsumerScopes(client_id: String): List[String] = {
    ConsumerDAO.findById(client_id) match {
      case Some(consumer) => ConsumerDAO.fetchScopeList(consumer)
      case None => List.empty

    }
  }

}

