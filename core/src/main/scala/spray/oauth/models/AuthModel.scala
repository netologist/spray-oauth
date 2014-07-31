package spray.oauth.models

import spray.http.Uri
import spray.oauth.{ AuthUser, AuthInfo, AccessToken }
import spray.oauth.utils.{ OAuth2Utils, OAuth2Parameters }
import OAuth2Parameters._
import spray.oauth.utils.OAuth2Utils._
import scala.Some
import spray.routing.RequestContext

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 4/15/14
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 4/15/14
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */

case class AuthRequest(response_type: ResponseType.Value,
    client_id: String,
    redirect_uri: String,
    scope: String,
    state: String = "",
    consumer_granted_scopes: String = "false",
    display: DisplayType.Value,
    access_type: AccessType.Value,
    approval_prompt: ApprovalPrompt.Value,
    approved_scopes: String = "",
    user_id: String = "") {
  require(!client_id.isEmpty, "client id must not be empty")
  require(consumerScopeEmptyRule, "scope must not be empty")
  require(DisplayType.values.exists(_.equals(display)), s"Invalid display: ${display}")
  require(ResponseType.values.exists(_.equals(response_type)), s"Invalid response_type: ${response_type}")
  require(AccessType.values.exists(_.equals(access_type)), s"Invalid access_type: ${access_type}")
  require(ApprovalPrompt.values.exists(_.equals(approval_prompt)), s"Invalid approval_prompt: ${approval_prompt}")

  private def consumerScopeEmptyRule: Boolean = {
    !(scope.isEmpty && !useConsumerGrantedScopes)
  }

  def optionalUser = {
    if (user_id.isEmpty) None else Some(user)
  }

  def user = AuthUser(user_id)

  def getClientIP(ctx: RequestContext): Option[String] = {
    // Just Only For Implicit Token
    if (getGrantType.equals(GrantType.Implicit)) OAuth2Utils.getClientIP(ctx) else None
  }

  def isRefreshable = {
    AccessType.OFFLINE == access_type
  }

  def getGrantType = {
    if (ResponseType.TOKEN == response_type) GrantType.Implicit else GrantType.AuthorizationCode
  }

  def approvalPromptForced = {
    ApprovalPrompt.FORCE == approval_prompt
  }

  def hasApprovedScopes = {
    !toScopeList(Some(approved_scopes)).isEmpty
  }

  def useConsumerGrantedScopes = {
    if (consumer_granted_scopes.isEmpty) false
    else {
      try {
        consumer_granted_scopes.toBoolean
      } catch {
        case e: Exception => false
      }
    }
  }
  def getScopeList = {
    OAuth2Utils.toScopeList(Some(scope))
  }
}

case class ApprovalForm(client_id: String, scopes: Map[String, Boolean])

object AuthResponse {
  sealed trait AuthResponse
  case class Approval(form: ApprovalForm) extends AuthResponse
  case class Error(error: String, error_description: Option[String]) extends AuthResponse
  case class Redirect(uri: Uri) extends AuthResponse
}

object DisplayType extends Enumeration {
  val PAGE = Value("page")
  val TOUCH = Value("touch")
  val DIALOG = Value("dialog")
  val POPUP = Value("popup")

  implicit def convertFromString(value: String): DisplayType.Value = try { DisplayType.withName(value) } catch { case ex: Exception => throw new Exception(s"Invalid display: ${value}") }
  implicit def convertToString(value: DisplayType.Value): String = value.toString
}

object ResponseType extends Enumeration {
  val CODE = Value("code")
  val TOKEN = Value("token")

  implicit def convertFromString(value: String): ResponseType.Value = try { ResponseType.withName(value) } catch { case ex: Exception => throw new Exception(s"Invalid response_type: ${value}") }
  implicit def convertToString(value: ResponseType.Value): String = value.toString
}

object AccessType extends Enumeration {
  val ONLINE = Value("online")
  val OFFLINE = Value("offline")

  implicit def convertFromString(value: String): AccessType.Value = try { AccessType.withName(value) } catch { case ex: Exception => AccessType.ONLINE }
  implicit def convertToString(value: AccessType.Value): String = value.toString
}

object ApprovalPrompt extends Enumeration {
  val AUTO = Value("auto")
  val FORCE = Value("force")

  implicit def convertFromString(value: String): ApprovalPrompt.Value = try { ApprovalPrompt.withName(value) } catch { case ex: Exception => ApprovalPrompt.AUTO }
  implicit def convertToString(value: ApprovalPrompt.Value): String = value.toString
}

