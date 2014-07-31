package spray.oauth.models

import spray.oauth.utils.{ OAuth2Utils, OAuth2Parameters }

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 3/6/14
 * Time: 9:34 AM
 * To change this template use File | Settings | File Templates.
 */

case class TokenRequest(grant_type: GrantType.Value,
    client_id: String,
    client_secret: String,
    scope: Option[String],
    code: Option[String],
    redirect_uri: Option[String],
    refresh_token: Option[String],
    username: Option[String],
    password: Option[String]) {

  def scopeRequired = {
    grant_type match {
      case GrantType.Password => true
      case GrantType.ClientCredentials => true
      case _ => false
    }
  }

  require(!grant_type.isEmpty, "grant_type must not be empty")
  require(GrantType.values.exists(_.equals(grant_type)), s"Invalid grant_type: ${grant_type}")
  require(required(GrantType.AuthorizationCode, code), "code required")

  require(required(GrantType.RefreshToken, refresh_token), "refresh_token required")

  require(required(GrantType.Password, scope), "scope required")
  require(required(GrantType.Password, username), "username required")
  require(required(GrantType.Password, password), "password required")

  require(required(GrantType.ClientCredentials, scope), "scope required")

  require(notEmpty(GrantType.AuthorizationCode, code), "code must not be empty")

  require(notEmpty(GrantType.RefreshToken, refresh_token), "refresh_token must not be empty")

  require(notEmpty(GrantType.Password, username), "username must not be empty")
  require(notEmpty(GrantType.Password, password), "password must not be empty")

  def notEmpty(grantType: GrantType.Value, member: Option[String]) = {
    if (grant_type.equals(grantType)) !member.getOrElse("").isEmpty else true
  }

  def getScopeList = {
    OAuth2Utils.toScopeList(scope)
  }

  def required(grantType: GrantType.Value, member: Option[String]) = {
    if (grant_type.equals(grantType)) !member.isEmpty else true
  }
}

object TokenResponse {
  sealed trait TokenResponse
  case class Token(token_type: String, access_token: String, refresh_token: Option[String], scope: Option[String], expires_in: Long) extends TokenResponse
  case class Code(code: String) extends TokenResponse
  case class Error(error: String, error_description: Option[String]) extends TokenResponse
}

case class ResponseException(error: String, error_description: Option[String] = None) extends Exception

object GrantType extends Enumeration {
  val AuthorizationCode = Value("authorization_code")
  val RefreshToken = Value("refresh_token")
  val ClientCredentials = Value("client_credentials")
  val Password = Value("password")
  val Implicit = Value("implicit")

  implicit def convertFromString(value: String): GrantType.Value = try { GrantType.withName(value) } catch { case ex: Exception => throw new Exception(s"invalid grant_type: ${value}") }
  implicit def convertToString(value: GrantType.Value): String = value.toString
}
