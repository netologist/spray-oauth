package spray.oauth

import spray.oauth.models.{ AuthRequest, TokenRequest }
import spray.oauth.models.TokenResponse.TokenResponse

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 4/16/14
 * Time: 4:13 PM
 * To change this template use File | Settings | File Templates.
 */
trait OAuth2GrantHandler {

  def authorizationCode(authInfo: AuthInfo)(implicit dataHandler: OAuth2DataHandler): TokenResponse

  def implicitToken(authInfo: AuthInfo)(implicit dataHandler: OAuth2DataHandler): TokenResponse

  def authorizationCode(request: TokenRequest)(implicit dataHandler: OAuth2DataHandler): TokenResponse

  def refreshToken(request: TokenRequest)(implicit dataHandler: OAuth2DataHandler): TokenResponse

  def clientCredentials(request: TokenRequest)(implicit dataHandler: OAuth2DataHandler): TokenResponse

  def password(request: TokenRequest)(implicit dataHandler: OAuth2DataHandler): TokenResponse

}
