package spray.oauth

import java.util.Date

import spray.oauth.models.GrantType
import spray.oauth.utils.OAuth2Utils

case class AuthClient(id: String, grantTypes: List[GrantType.Value], scopes: List[String]) {
  def hasScope(scope: Option[String]): Boolean = {
    hasScope(OAuth2Utils.toScopeList(scope))
  }

  def hasScope(scope: List[String]): Boolean = {
    (scope diff this.scopes).isEmpty
  }

  def hasGrant(grantType: GrantType.Value): Boolean = {
    grantTypes.exists(x => x.equals(grantType))
  }
}

case class AuthUser(id: String)

case class AccessToken(token: String, refreshToken: Option[String], tokenType: String, scope: Option[String], expiresIn: Long, createdAt: Date)

case class AuthInfo(user: Option[AuthUser], clientId: Option[String], scope: Option[String], redirectUri: Option[String], refreshable: Boolean, grantType: GrantType.Value, remoteAddress: Option[String] = None)

trait OAuth2DataHandler {

  /* Check Credentials */

  def getUser(username: String, password: String): Option[AuthUser]

  def getClient(clientId: String, clientSecret: String): Option[AuthClient]

  def getClient(clientId: String): Option[AuthClient]

  def checkUserCredentials(username: String, password: String): Boolean

  def checkConsumerCredentials(clientId: String, clientSecret: String): Boolean

  /* Authorization Code */

  def createCode(authInfo: AuthInfo): Option[String]

  def deleteCode(code: String): Unit

  def findAuthInfoByCode(code: String): Option[AuthInfo]

  /* Auth Info Methods */

  /* Access Token */

  def refreshAccessToken(authInfo: AuthInfo, refreshToken: String): Option[AccessToken]

  def createAccessToken(authInfo: AuthInfo): Option[AccessToken]

  def findAccessToken(info: AuthInfo): Option[AccessToken]

  def findAuthInfoByAccessToken(accessToken: String): Option[AuthInfo]

  def findAuthInfoByRefreshToken(refreshToken: String): Option[AuthInfo]

  def findAuthInfoByUser(clientId: String, user: AuthUser, grantType: GrantType.Value): Option[AuthInfo]

  def findAuthInfoByClient(clientId: String): Option[AuthInfo]

  /*

    def findAccessToken(token: String): Option[AccessToken]

    def findAuthInfoByAccessToken(accessToken: AccessToken): Option[AuthInfo[U]]
  */

  /* Helper Methods */

  def isAccessTokenExpired(accessToken: AccessToken): Boolean = {
    val now = System.currentTimeMillis()
    (accessToken.createdAt.getTime + accessToken.expiresIn * 1000) <= now
  }
}
