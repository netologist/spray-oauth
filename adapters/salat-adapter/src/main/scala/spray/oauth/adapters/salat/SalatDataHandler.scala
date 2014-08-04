package spray.oauth.adapters.salat

import spray.oauth._
import com.mongodb.casbah.Imports._
import scala.Some
import spray.oauth.adapters.salat.models.{ CodeDAO, TokenDAO, UserDAO, ConsumerDAO }
import spray.oauth.models.GrantType
import com.mongodb.casbah.Imports

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 6/3/14
 * Time: 8:50 AM
 * To change this template use File | Settings | File Templates.
 */
object SalatDataHandler extends OAuth2DataHandler {

  override def findAuthInfoByRefreshToken(refreshToken: String): Option[AuthInfo] = {
    TokenDAO.findAuthInfoByRefreshToken(refreshToken)
  }

  override def deleteCode(code: String): Unit = {
    CodeDAO.deleteCode(code)
  }

  override def findAuthInfoByCode(code: String): Option[AuthInfo] = {
    CodeDAO.findAuthInfoByCode(code)
  }

  override def findAuthInfoByUser(clientId: String, user: AuthUser, grantType: GrantType.Value): Option[AuthInfo] = {
    TokenDAO.findUserToken(clientId, user, grantType)
  }

  override def findAuthInfoByClient(clientId: String): Option[AuthInfo] = {
    TokenDAO.findConsumerToken(clientId)
  }

  /*
   def findAuthInfoByConsumer(clientId: String, clientSecret: String, scope: Option[String]): Option[AuthInfo] = {
    ConsumerDAO.findWithCredentialsWithScope(clientId, clientSecret, scope) map { consumer =>
      val refreshable = ConsumerDAO.fetchGrantList(consumer).contains(GrantType.RefreshToken.toString)
      AuthInfo(None, Some(consumer.client_id), scope, None, refreshable)
    }
  }

  // FIXME: Design issue
   def findAuthInfoByUser(clientId: String, scope: Option[String], user: ObjectId): Option[AuthInfo[Imports.ObjectId]] = {
    TokenDAO.findByUser(clientId, user) map { token =>
      AuthInfo(Some(user), Some(clientId), token.toScopeString, None, token.refresh_token.isDefined)
    }
  }

  // FIXME: Design issue
   def findAuthInfoByUser(clientId: String, clientSecret: String, scope: Option[String], user: ObjectId): Option[AuthInfo[Imports.ObjectId]] = {
    ConsumerDAO.findWithCredentialsWithScope(clientId, clientSecret, scope) map { consumer =>
      val refreshable = ConsumerDAO.fetchGrantList(consumer).contains(GrantType.RefreshToken.toString)
      AuthInfo(Some(user), Some(consumer.client_id), scope, None, refreshable)
    }
  }
  */

  override def createAccessToken(authInfo: AuthInfo): Option[AccessToken] = {
    TokenDAO.createToken(authInfo).map { x => x.toAccessToken }
  }

  override def refreshAccessToken(authInfo: AuthInfo, refreshToken: String): Option[AccessToken] = {
    TokenDAO.findByAuthInfo(authInfo) match {
      case None => createAccessToken(authInfo)
      case Some(token) => TokenDAO.renewToken(authInfo, token).map { x => x.toAccessToken }
    }
  }

  override def createCode(authInfo: AuthInfo): Option[String] = {
    CodeDAO.createCode(authInfo).map { x => x.code.toString }
  }

  override def findAccessToken(authInfo: AuthInfo): Option[AccessToken] = {
    TokenDAO.findByAuthInfo(authInfo).map { x => x.toAccessToken }
  }

  override def findAuthInfoByAccessToken(token: String): Option[AuthInfo] = {
    TokenDAO.findAuthInfoByAccessToken(token)
  }

  override def getUser(username: String, password: String): Option[AuthUser] = {
    UserDAO.findWithCredentials(username, password).map(user => AuthUser(user.id.toString))
  }

  override def checkUserCredentials(username: String, password: String): Boolean = {
    getUser(username, password).isDefined
  }

  override def getClient(clientId: String, clientSecret: String): Option[AuthClient] = {
    ConsumerDAO.findWithCredentials(clientId, clientSecret) map { consumer =>
      AuthClient(consumer.client_id,
        ConsumerDAO.fetchGrantList(consumer).map(x => GrantType.convertFromString(x)),
        ConsumerDAO.fetchScopeList(consumer))
    }
  }

  override def getClient(clientId: String): Option[AuthClient] = {
    ConsumerDAO.findById(clientId) map { consumer =>
      AuthClient(consumer.client_id,
        ConsumerDAO.fetchGrantList(consumer).map(x => GrantType.convertFromString(x)),
        ConsumerDAO.fetchScopeList(consumer))
    }
  }

  override def checkConsumerCredentials(clientId: String, clientSecret: String): Boolean = {
    getClient(clientId, clientSecret).isDefined
  }
}

