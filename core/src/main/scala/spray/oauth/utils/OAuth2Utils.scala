package spray.oauth.utils

import spray.oauth.utils.OAuth2Parameters._
import spray.routing.RequestContext
import spray.http.HttpHeader
import spray.http.HttpHeaders.{ `Remote-Address`, `X-Forwarded-For` }
import java.security.MessageDigest

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 4/18/14
 * Time: 12:49 PM
 * To change this template use File | Settings | File Templates.
 */
object OAuth2Utils {

  def isSameScopes(requestedScopes: Option[String], authorizedScopes: Option[String]): Boolean = {
    val tokenScopeList = OAuth2Utils.toScopeList(requestedScopes)
    val authInfoScopeList = OAuth2Utils.toScopeList(authorizedScopes)

    tokenScopeList sameElements authInfoScopeList
  }

  def isNotSameScopes(requestedScopes: Option[String], authorizedScopes: Option[String]): Boolean = {
    !isSameScopes(requestedScopes, authorizedScopes)
  }

  def toScopeList(scope: Option[String]): List[String] = {
    scope.getOrElse("").split(SCOPE_SEPARATOR).distinct.toList.filter(x => !x.isEmpty)
  }

  def getClientIP(ctx: RequestContext): Option[String] = {
    val found: Option[HttpHeader] =
      ctx.request.headers.find {
        case `X-Forwarded-For`(Seq(address, _*)) => true
        case `Remote-Address`(address) => true
        case h if h.is("x-real-ip") => true
      }

    if (found.isDefined) Some(found.get.value) else None
  }

  def md5(s: String) = {
    MessageDigest.getInstance("MD5").digest(s.getBytes)
  }
}
