package spray.oauth.authentication

import spray.oauth.{ OAuth2GrantHandler, OAuth2DataHandler, AuthInfo, AccessToken }
import spray.routing.authentication.ContextAuthenticator
import scala.concurrent.{ ExecutionContext, Future }
import spray.routing.{ Rejection, RequestContext, AuthenticationFailedRejection }
import spray.routing.AuthenticationFailedRejection.{ CredentialsMissing, CredentialsRejected }
import spray.oauth.utils.OAuth2Utils._
import spray.oauth.AuthInfo
import scala.Some
import spray.http.HttpHeaders.{ Authorization }
import ExecutionContext.Implicits.global

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 6/12/14
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
trait ResourceAuthenticator {

  implicit def dataHandler: OAuth2DataHandler

  implicit def grantHandler: OAuth2GrantHandler

  def tokenAuthenticator: ContextAuthenticator[AuthInfo] = { ctx =>
    fetchAccessToken(ctx) match {
      case Some(token) =>
        dataHandler.findAuthInfoByAccessToken(token) match {
          case Some(info) => Future(Right(info))
          case None => Future(Left(AuthenticationFailedRejection(CredentialsRejected, List())))
        }
      case None => Future(Left(AuthenticationFailedRejection(CredentialsMissing, List())))
    }
  }

  def allowedScopes(info: AuthInfo, scopes: String*): Boolean = {
    !(toScopeList(info.scope) intersect scopes).isEmpty
  }

  /* Helpers */

  private def fetchAccessToken(ctx: RequestContext): Option[String] = {
    val found = ctx.request.header[`Authorization`]
    if (found.isDefined) {
      val REGEXP_AUTHORIZATION = """^\s*(OAuth|OAuth2|Bearer)\s+([^\s\,]*)""".r
      val matcher = REGEXP_AUTHORIZATION.findFirstMatchIn(found.get.value)
      if (matcher.isDefined && matcher.get.groupCount >= 2) Some(matcher.get.group(2)) else None
    } else ctx.request.uri.query.get("access_token")
  }
}
