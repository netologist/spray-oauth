package spray.oauth.adapters.inmemory

import spray.oauth.adapters.inmemory.models.{ ConsumerDAO, UserDAO, CodeDAO, TokenDAO }
import spray.oauth.models.GrantType
import spray.oauth._

/**
 * Created by hasanozgan on 03/06/14.
 */
private object InMemoryDataHandler extends OAuth2DataHandler {

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
    ConsumerDAO.findBy(p => p.client_id.equals(clientId)) map { consumer =>
      AuthClient(consumer.client_id,
        ConsumerDAO.fetchGrantList(consumer).map(x => GrantType.convertFromString(x)),
        ConsumerDAO.fetchScopeList(consumer))
    }
  }

  override def checkConsumerCredentials(clientId: String, clientSecret: String): Boolean = {
    getClient(clientId, clientSecret).isDefined
  }
}
