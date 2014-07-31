package spray.oauth.adapters.inmemory.models

import com.github.nscala_time.time.Imports._
import spray.oauth.adapters.inmemory.utils.{ Entity, DAO }
import spray.oauth.utils.OAuth2Utils

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 3/7/14
 * Time: 10:44 AM
 * To change this template use File | Settings | File Templates.
 */

case class Consumer(id: Long,
    fk_role: Long,
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
    deleted: Boolean = false) extends Entity(id) {

  def client_id = id.toString
}

object ConsumerDAO extends DAO[Consumer] {

  private def mergedScopeList(consumer: Consumer, role: Role) = (consumer.scopes ::: role.scopes).distinct.toList
  private def mergedGrantList(consumer: Consumer, role: Role) = (consumer.grants ::: role.grants).distinct.toList
  private def getRole(consumer: Consumer, role: Option[Role]) = if (role.isEmpty) RoleDAO.findBy(p => p.id == consumer.fk_role) else role

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
    findBy(p => p.client_id.equals(client_id) && p.client_secret.equals(client_secret))
  }

  def findWithClientIdAndScope(client_id: String, scope: Option[String]): Option[Consumer] = {
    findBy(p => p.client_id.equals(client_id)) match {
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
    ConsumerDAO.findBy(p => p.client_id.equals(client_id)) match {
      case Some(consumer) => {

        ConsumerDAO.fetchScopeList(consumer)
      }
      case None => List.empty

    }
  }

}

