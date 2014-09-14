package spray.oauth.adapters.slick

import spray.oauth.adapters.slick.models.UserDAO
import spray.oauth.models.GrantType
import spray.oauth._

/**
 * Created by hasanozgan on 04/08/14.
 */
object SlickDataHandler extends OAuth2DataHandler {
  override def getUser(username: String, password: String): Option[AuthUser] = ???

  override def findAuthInfoByClient(clientId: String): Option[AuthInfo] = ???

  override def deleteCode(code: String): Unit = ???

  override def createAccessToken(authInfo: AuthInfo): Option[AccessToken] = ???

  override def getClient(clientId: String, clientSecret: String): Option[AuthClient] = ???

  override def getClient(clientId: String): Option[AuthClient] = ???

  override def createCode(authInfo: AuthInfo): Option[String] = ???

  override def findAuthInfoByRefreshToken(refreshToken: String): Option[AuthInfo] = ???

  override def refreshAccessToken(authInfo: AuthInfo, refreshToken: String): Option[AccessToken] = ???

  override def checkUserCredentials(username: String, password: String): Boolean = ???

  override def findAuthInfoByUser(clientId: String, user: AuthUser, grantType: GrantType.Value): Option[AuthInfo] = ???

  override def findAuthInfoByAccessToken(accessToken: String): Option[AuthInfo] = ???

  override def findAuthInfoByCode(code: String): Option[AuthInfo] = ???

  override def checkConsumerCredentials(clientId: String, clientSecret: String): Boolean = ???

  override def findAccessToken(info: AuthInfo): Option[AccessToken] = ???
}
