package spray.oauth.endpoints

import spray.routing.{ RequestContext, HttpService }
import spray.oauth.directives.OAuth2Directives
import spray.http.{ HttpCredentials, HttpHeader, HttpRequest, StatusCodes }
import spray.oauth.models.AuthResponse.{ Error, Redirect, Approval }
import spray.routing.authentication._
import scala.concurrent.{ ExecutionContext, Future }
import ExecutionContext.Implicits.global
import spray.routing.authentication.UserPass
import spray.oauth.models.{ ApprovalForm, AuthResponse, AuthRequest }
import spray.oauth.{ AuthUser, AuthInfo }
import spray.oauth.utils.OAuth2Parameters._
import spray.oauth.utils.OAuth2Utils
import spray.http._
import MediaTypes._

/**
 * Created by hasanozgan on 03/06/14.
 */
trait AuthorizeService extends HttpService with OAuth2Directives {

  case class User(id: String, name: String)

  def myUserPassAuthenticator(userPass: Option[UserPass]): Future[Option[AuthUser]] = {
    Future {
      if (userPass.isDefined)
        dataHandler.getUser(userPass.get.user, userPass.get.pass)
      else None
      //dataHandler.findUser(userPass.get.g
      /**
       * if (userPass.exists(up => up.user == "user" && up.pass == "pass")) dataHandler.findUser(user)
       * else None
       */
    }
  }

  val defaultAuthorizeRoutes =
    path("authorize") {
      authenticate(BasicAuth(myUserPassAuthenticator _, realm = "secure site")) { user =>
        fetchAuthRequest(user) { request =>
          //protectRequest(request, user) { csrf_token =>
          authHandler(request) {
            case AuthResponse.Approval(form) => respondWithMediaType(`text/html`) {
              complete(renderApprovalForm(form))
            }
            case AuthResponse.Redirect(uri) => redirect(uri, StatusCodes.TemporaryRedirect)
            case AuthResponse.Error(err, desc) => complete(s"ERROR: ${err}") //render("tpl/approvalPage", request)
          }
          //          }
        }
      }
    } ~
      path("login") {
        get {
          complete("OK")
        }
      }

  private def renderApprovalForm(form: ApprovalForm) = {

    s"""<html>
      <body>
        <h1>OAuth Approval</h1>
        <p>Do you authorize '${form.client_id}' to access your protected resources?</p>
        <form id='confirmationForm' name='confirmationForm' method='post'>
          ${renderScopes(form)}
          <input class="btn btn-primary" type="submit" value="Approve" />
          <input class="btn" type="submit" value="Cancel" />
        </form>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.0/jquery.min.js"></script>
        <script type="application/javascript">
          $$("form input:submit[value=Cancel]").on("click", function(){
            $$("form input:radio[value=false]").attr("checked", "checked");
          });
        </script>
      </body>
    </html>"""
  }

  private def renderScopes(form: ApprovalForm) = {
    form.scopes.map(scope =>
      s"""<li>
            <div class="form-group">
						  ${scope._1}:
              <label><input name="${APPROVAL_FORM_PREFIX_FOR_SCOPE_KEY}${scope._1}" type="radio" ${scopeSelected(true, scope._2)} value="true">Approve</input></label>
              <label><input name="${APPROVAL_FORM_PREFIX_FOR_SCOPE_KEY}${scope._1}" type="radio" ${scopeSelected(false, scope._2)} value="false">Deny</input></label>
            </div>
          </li>""").mkString("<ul class=\"list-unstyled\">", "", "</ul>")
  }

  def scopeSelected(inputType: Boolean, scopeStatus: Boolean) = {
    if (inputType == scopeStatus) "checked=checked" else ""
  }

}