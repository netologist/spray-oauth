package spray.oauth.utils

import spray.http.HttpMethods
import spray.oauth.models.AuthRequest
import spray.oauth.utils.OAuth2Parameters._
import spray.routing.{ MalformedHeaderRejection, MissingHeaderRejection, RequestContext }

/**
 * Created by hasan.ozgan on 7/7/2014.
 * TODO...
 */
class CSRFUtils[U] {
  def generateCsrfToken(request: AuthRequest, user: U): Option[String] = {
    if (!APPROVAL_FORM_CSRF_ENABLED) None
    else {
      val crcKey = TokenGenerator.hash(8)
      val hash = HashUtils.md5("%s|%s|%s|%s|%s|%s".format(request.client_id, user, request.scope, request.getGrantType, crcKey, OAuth2Parameters.SECRET))
      Some(crcKey + hash.substring(8))
    }
  }

  def confirmCsrfToken(csrf: String, request: AuthRequest, user: U): Boolean = {
    if (!APPROVAL_FORM_CSRF_ENABLED) true
    else if (csrf.isEmpty || csrf.length < 8) false
    else {
      val crcKey = csrf.substring(0, 8)
      val hash = HashUtils.md5("%s|%s|%s|%s|%s|%s".format(request.client_id, user, request.scope, request.getGrantType, crcKey, OAuth2Parameters.SECRET))

      csrf.substring(8).equals(hash.substring(8))
    }
    true
  }

  def checkCsrfToken(request: AuthRequest, ctx: RequestContext, user: U): Boolean = {
    var status = true
    val csrfList = ctx.request.headers.filter(x => x.is("X-XSRF-TOKEN")).map(x => x.value.toString)

    if (APPROVAL_FORM_CSRF_ENABLED && isPostMethod(ctx)) {
      if (csrfList.isEmpty) {
        status = false
        ctx.reject(MissingHeaderRejection("X-XSRF-TOKEN"))
      } else if (confirmCsrfToken(csrfList.head, request, user)) {
        status = false
        ctx.reject(MalformedHeaderRejection("X-XSRF-TOKEN", "token is invalid"))
      }
    }

    status
  }

  private def isPostMethod(ctx: RequestContext) = ctx.request.method == HttpMethods.POST
}
