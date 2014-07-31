package spray.oauth.utils

import com.typesafe.config.ConfigFactory

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 4/18/14
 * Time: 10:40 AM
 * To change this template use File | Settings | File Templates.
 */
object OAuth2Parameters {
  lazy val conf = ConfigFactory.load()

  val SCOPE_SEPARATOR = conf.getString("spray.oauth2.scope-separator")
  val TOKEN_DURATION = conf.getMilliseconds("spray.oauth2.token-duration")
  val TOKEN_LENGTH = conf.getInt("spray.oauth2.token-length")
  val CODE_DURATION = conf.getMilliseconds("spray.oauth2.code-duration")
  val CODE_LENGTH = conf.getInt("spray.oauth2.code-length")
  val REFRESH_TOKEN_LENGTH = conf.getInt("spray.oauth2.refresh-token-length")
  val CONSUMER_SECRET_LENGTH = conf.getInt("spray.oauth2.consumer-secret-length")
  val SHOW_SCOPE = conf.getBoolean("spray.oauth2.show-scope")
  val SECRET = conf.getString("spray.oauth2.secret")

  val APPLICATION_SESSION_KEY = conf.getString("spray.oauth2.application.session-key")
  val APPLICATION_SESSION_COOKIE_DOMAIN = getCookieDomain

  val RESOURCE_HEADERS_VERSIONING = conf.getString("spray.oauth2.resource.headers.versioning")

  val APPROVAL_FORM_CSRF_ENABLED = conf.getBoolean("spray.oauth2.approval-form.csrf-enabled")
  val APPROVAL_FORM_CSRF_TOKEN_KEY = conf.getString("spray.oauth2.approval-form.csrf-token-key")
  val APPROVAL_FORM_PREFIX_FOR_SCOPE_KEY = conf.getString("spray.oauth2.approval-form.prefix-for-scope-key")

  private def getCookieDomain = {
    val domain = conf.getString("spray.oauth2.application.session-cookie-domain")
    if (domain.isEmpty) None else Some(domain)
  }
}
