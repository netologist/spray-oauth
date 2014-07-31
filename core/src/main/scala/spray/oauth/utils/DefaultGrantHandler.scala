package spray.oauth.utils

import spray.oauth._
import spray.oauth.models._
import spray.oauth.AccessToken
import spray.oauth.models.TokenResponse.{ Code, Token, TokenResponse }
import scala.Some
import spray.oauth.utils.OAuth2Parameters._
import spray.oauth.models.TokenResponse.TokenResponse
import spray.oauth.models.TokenResponse
import spray.oauth.models.ResponseException
import spray.oauth.AccessToken
import spray.oauth.models.TokenResponse.Code
import spray.oauth.AuthInfo
import spray.oauth.models.TokenResponse.Token
import scala.Some
import spray.oauth.models.TokenRequest

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 4/16/14
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
object DefaultGrantHandler extends OAuth2GrantHandler {

  override def authorizationCode(request: TokenRequest)(implicit dataHandler: OAuth2DataHandler): TokenResponse = {
    dataHandler.findAuthInfoByCode(request.code.get) match {
      case Some(authInfo) if authInfo.clientId.exists(x => x.equals(request.client_id)) => {
        dataHandler.deleteCode(request.code.get)
        issueAccessToken(authInfo)
      }
      case _ => TokenResponse.Error("invalid_grant", None)
    }
  }

  override def authorizationCode(authInfo: AuthInfo)(implicit dataHandler: OAuth2DataHandler): TokenResponse = {
    issueAuthorizationCode(authInfo)
  }

  override def clientCredentials(request: TokenRequest)(implicit dataHandler: OAuth2DataHandler): TokenResponse = {
    val authInfo = AuthInfo(None, Some(request.client_id), request.scope, None, refreshable = true, GrantType.ClientCredentials, None)

    issueAccessToken(authInfo)
  }

  override def password(request: TokenRequest)(implicit dataHandler: OAuth2DataHandler): TokenResponse = {
    val client = dataHandler.getClient(request.client_id).getOrElse(throw new ResponseException("invalid_client"))
    val user = dataHandler.getUser(request.username.get, request.password.get).getOrElse(throw new ResponseException("InvalidGrant"))
    val authInfo = AuthInfo(Some(user), Some(request.client_id), request.scope, None, refreshable = client.hasGrant(GrantType.RefreshToken), GrantType.Password, None)

    issueAccessToken(authInfo, showRefreshToken = true)
  }

  override def refreshToken(request: TokenRequest)(implicit dataHandler: OAuth2DataHandler): TokenResponse = {
    val refreshToken = request.refresh_token.getOrElse("")
    val authInfo = dataHandler.findAuthInfoByRefreshToken(refreshToken).getOrElse(throw new ResponseException("InvalidToken"))

    issueAccessToken(authInfo, showRefreshToken = false)
  }

  override def implicitToken(authInfo: AuthInfo)(implicit dataHandler: OAuth2DataHandler): TokenResponse = {
    issueAccessToken(authInfo, showRefreshToken = false)
  }

  /* Helper Methods */

  private def issueAuthorizationCode(authInfo: AuthInfo)(implicit dataHandler: OAuth2DataHandler): TokenResponse.TokenResponse = {
    dataHandler.createCode(authInfo) match {
      case Some(code) => TokenResponse.Code(code)
      case _ => TokenResponse.Error("internal_error", None)
    }
  }

  private def issueAccessToken(authInfo: AuthInfo, showRefreshToken: Boolean = true)(implicit dataHandler: OAuth2DataHandler): TokenResponse = {
    val accessToken = dataHandler.findAccessToken(authInfo) match {
      case Some(token) if dataHandler.isAccessTokenExpired(token) || OAuth2Utils.isNotSameScopes(token.scope, authInfo.scope) =>
        {
          token.refreshToken match {
            case Some(rt) => dataHandler.refreshAccessToken(authInfo, rt).getOrElse(throw new Exception("refresh_token is not generated"))
            case None => dataHandler.createAccessToken(authInfo).getOrElse(throw new Exception("token is not generated"))
          }
        }
      case Some(token) => token
      case _ => dataHandler.createAccessToken(authInfo).getOrElse(throw new Exception("token is not generated"))
    }

    toTokenResponse(accessToken, authInfo.refreshable && showRefreshToken)
  }

  private def toTokenResponse(accessToken: AccessToken, showRefreshToken: Boolean = true): TokenResponse = {
    Token(
      accessToken.tokenType,
      accessToken.token,
      if (showRefreshToken) accessToken.refreshToken else None,
      if (SHOW_SCOPE) accessToken.scope else None,
      accessToken.expiresIn
    )
  }

}
