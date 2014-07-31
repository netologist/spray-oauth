package spray.oauth.directives

import spray.routing.{ AuthenticationFailedRejection, Directive1, Directive0, StandardRoute }
import spray.http.{ HttpHeaders, DateTime, HttpCookie }
import spray.routing.directives._
import spray.routing._
import spray.httpx.marshalling.ToResponseMarshallable
import scala.concurrent.Future
import spray.routing.authentication._
import spray.routing.RequestContext
import spray.routing.directives.CookieDirectives._
import spray.routing.MissingCookieRejection
import scala.Some

import spray.util._
import spray.http._
import HttpHeaders._
import spray.routing.MissingCookieRejection
import scala.Some
import spray.routing
import shapeless.HNil
import spray.routing.AuthenticationFailedRejection.CredentialsRejected
import spray.oauth.utils.{ OAuth2Parameters, TokenGenerator }
import spray.oauth.rejections.SecurePageRejection

/**
 * Created by hasanozgan on 20/03/14.
 */
trait SessionDirectives {
  import BasicDirectives._
  import CookieDirectives._
  import RouteDirectives._
  import HeaderDirectives._
  import RespondWithDirectives._

  def session: Directive1[HttpCookie] =
    extract {
      ctx =>
        {
          val sid = findSessionCookie(ctx)

          if (!sid.isDefined)
            ctx.reject(SecurePageRejection(ctx.request.uri))

          sid.get
        }
    }

  def deleteSession: Directive0 = {
    deleteCookie(name = OAuth2Parameters.APPLICATION_SESSION_KEY, path = "/")
  }

  def createSession: Directive0 = {
    val token = TokenGenerator.bearer
    val cookie = HttpCookie(name = OAuth2Parameters.APPLICATION_SESSION_KEY, domain = OAuth2Parameters.APPLICATION_SESSION_COOKIE_DOMAIN, content = token, path = Some("/"), secure = true)
    setCookie(cookie)
  }

  private def findSessionCookie(ctx: RequestContext) = {
    ctx.request.cookies.find(_.name equals OAuth2Parameters.APPLICATION_SESSION_KEY)
  }

}
