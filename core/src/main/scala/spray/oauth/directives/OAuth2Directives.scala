package spray.oauth.directives

import spray.routing._
import spray.routing.directives._
import shapeless.HNil
import scala._

import spray.http._
import spray.http.HttpHeaders.{ Authorization, `X-Forwarded-For`, `Remote-Address` }
import spray.oauth.utils.OAuth2Utils._
import spray.oauth.utils.OAuth2Parameters._

import spray.oauth._
import spray.oauth.models._
import spray.oauth.models.AuthResponse.AuthResponse

import spray.oauth.models.AuthRequest
import spray.oauth.models.AuthResponse.Approval
import spray.routing.RequestContext

import scala.Some
import shapeless.::
import spray.oauth.models.TokenRequest
import spray.oauth.models.ResponseException
import spray.routing.MissingCookieRejection
import spray.oauth.AuthInfo
import spray.oauth.models.TokenResponse.TokenResponse
import spray.oauth.models.TokenResponse
import scala.util.Try
import spray.oauth.utils.{ TokenGenerator, OAuth2Utils, OAuth2Parameters }

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 4/15/14
 * Time: 9:57 AM
 * To change this template use File | Settings | File Templates.
 */
trait OAuth2Directives extends FormFieldDirectives with ParameterDirectives {

  import BasicDirectives._
  import AnyParamDirectives._
  import HeaderDirectives._
  import MethodDirectives._
  import MarshallingDirectives._

  implicit def dataHandler: OAuth2DataHandler

  implicit def grantHandler: OAuth2GrantHandler

  /**
   *
   * @return
   */
  def fetchTokenRequest: Directive1[TokenRequest] =
    post & formFields('grant_type.as[GrantType.Value],
      'client_id,
      'client_secret,
      'scope?,
      'code?,
      'redirect_uri?,
      'refresh_token?,
      'username?,
      'password?).as(TokenRequest)

  /**
   *
   * @param request
   * @return
   */
  def grantHandler(request: TokenRequest): Directive1[TokenResponse] =
    extract { ctx =>
      dataHandler.getClient(request.client_id, request.client_secret) match {
        case None => TokenResponse.Error("invalid_client", Some(s"invalid client_id '${request.client_id}'"))
        case Some(client) if !client.hasGrant(request.grant_type) => TokenResponse.Error("invalid_grant", Some(s"invalid grant_type '${request.grant_type}' for this consumer"))
        case Some(client) if request.scopeRequired && !client.hasScope(request.getScopeList) => {
          val unknownScopes = request.getScopeList diff client.scopes
          TokenResponse.Error("invalid_scope", Some(s"invalid scopes '${unknownScopes.mkString(",")}' for this consumer"))
        }
        case Some(client) =>
          try {
            request.grant_type match {
              case GrantType.Password => grantHandler.password(request)
              case GrantType.ClientCredentials => grantHandler.clientCredentials(request)
              case GrantType.RefreshToken => grantHandler.refreshToken(request)
              case GrantType.AuthorizationCode => grantHandler.authorizationCode(request)
            }
          } catch {
            case e: ResponseException => TokenResponse.Error(e.error, e.error_description)
          }
      }
    }

  def approveForm: Directive1[List[String]] =
    entity(as[FormData]) map {
      case formData: FormData => extractApprovedScopes(formData) :: HNil
    }

  def authRequest(user: AuthUser, scopes: String): Directive1[AuthRequest] =
    parameters('response_type.as[ResponseType.Value],
      'client_id.as[String],
      'redirect_uri.as[String],
      'scope.as[String] ? "",
      'state.as[String] ? "",
      'consumer_granted_scopes.as[String] ? "false",
      'display.as[DisplayType.Value] ? DisplayType.PAGE,
      'access_type.as[AccessType.Value] ? AccessType.ONLINE,
      'approval_prompt.as[ApprovalPrompt.Value] ? ApprovalPrompt.AUTO,
      'approved_scopes.as[String] ? scopes,
      'user_id.as[AuthUser] ? user.id).as(AuthRequest)

  def fetchAuthRequest(user: AuthUser): Directive1[AuthRequest] =
    approveForm hflatMap {
      case scopes :: HNil =>
        authRequest(user, scopes.asInstanceOf[List[String]].mkString(" "))
    }

  /**
   *
   * @param request
   * @return
   */
  def authHandler(request: AuthRequest): Directive1[AuthResponse] =
    extract { ctx =>
      try {
        dataHandler.getClient(request.client_id) match {
          case None => AuthResponse.Error("invalid_client", None)
          case Some(client) if !client.hasGrant(request.getGrantType) => AuthResponse.Error("invalid_grant", Some(s"invalid grant_type '${request.getGrantType}' for this consumer"))
          case Some(client) if !client.hasScope(request.getScopeList) => {
            val unknownScopes = request.getScopeList diff client.scopes
            throw new ResponseException(s"invalid scopes '${unknownScopes.mkString(",")}' for this consumer")
          }
          case Some(client) => {

            val userScopesInfo = dataHandler.findAuthInfoByUser(request.client_id, request.user, request.getGrantType) match {
              case Some(info) => (info.scope, OAuth2Utils.toScopeList(info.scope))
              case None => (None, List.empty)
            }

            val scopeMap = generateScopeMap(request.getScopeList, userScopesInfo._2)
            val requiredScopes = scopeMap.filter(x => !x._2)

            if (isGetMethod(ctx) && (request.approvalPromptForced || OAuth2Utils.isNotSameScopes(userScopesInfo._1, Some(request.scope)) || requiredScopes.size > 0)) {
              Approval(ApprovalForm(request.client_id, scopeMap))
            } else if (isPostMethod(ctx) && !request.hasApprovedScopes) {
              AuthResponse.Error("canceled_by_user", Some("canceled by user"))
            } else {
              val approvedScopes = if (request.approved_scopes.isEmpty) request.scope else request.approved_scopes
              val authInfo = AuthInfo(request.optionalUser, Some(request.client_id), Some(approvedScopes), Some(request.redirect_uri), request.isRefreshable, request.getGrantType, request.getClientIP(ctx))

              val tokenResponse =
                request.getGrantType match {
                  case GrantType.Implicit => grantHandler.implicitToken(authInfo)
                  case GrantType.AuthorizationCode => grantHandler.authorizationCode(authInfo)
                }

              AuthResponse.Redirect(generateUri(request.redirect_uri, request.state, tokenResponse))
            }
          }
        }
      } catch {
        case ex: ResponseException => AuthResponse.Error(ex.error, ex.error_description)
        case e: Exception => {
          e.printStackTrace()
          AuthResponse.Error("BadRequest", Some(e.getMessage))
        }
      }
    }

  /* Helpers */
  private def isGetMethod(ctx: RequestContext) = ctx.request.method == HttpMethods.GET

  private def isPostMethod(ctx: RequestContext) = ctx.request.method == HttpMethods.POST

  private def generateScopeMap(requestScopes: List[String], userScopes: List[String]): Map[String, Boolean] = {
    val requestedScopeMap = requestScopes map { t => (t, false) } toMap
    val userScopeMap = userScopes map { t => (t, true) } toMap

    mergeMap(requestedScopeMap, userScopeMap)
  }

  private def mergeMap[K, V](ts: Map[K, V], xs: Map[K, V]): Map[K, V] =
    (ts /: xs) {
      case (acc, entry) =>
        if (entry._2 == true) acc + entry else acc
    }

  private def generateUri(redirectUri: String, state: String, response: TokenResponse): Uri = {
    val r = response match {
      case token: TokenResponse.Token => {
        val scopeParam = token.scope.fold("")(x => s"&scope=${x}")
        s"#access_token=${token.access_token}&token_type=${token.token_type}&expires_in=${token.expires_in}${scopeParam}"
      }
      case code: TokenResponse.Code => s"?code=${code.code}"
      case err: TokenResponse.Error => s"?error=${err.error}&error_description=${err.error_description.getOrElse("N/A")}"
    }

    redirectUri + r
  }

  private def extractApprovedScopes(f: FormData): List[String] = {
    f.fields.toMap
      .filter(x => x._1.startsWith(APPROVAL_FORM_PREFIX_FOR_SCOPE_KEY) && Try(x._2.toBoolean).getOrElse(false))
      .map(x => x._1.stripPrefix(APPROVAL_FORM_PREFIX_FOR_SCOPE_KEY))
      .toList
  }
}
