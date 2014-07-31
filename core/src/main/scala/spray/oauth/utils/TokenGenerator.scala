package spray.oauth.utils

import java.security.SecureRandom
import spray.oauth.utils.OAuth2Parameters._

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 3/25/14
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
object TokenGenerator {
  val TOKEN_HASH_CHARS = "0123456789abcdef"
  val TOKEN_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-._"
  val secureRandom = new SecureRandom()

  def hash(length: Int): String =
    if (length == 0) ""
    else TOKEN_HASH_CHARS(secureRandom.nextInt(TOKEN_HASH_CHARS.length())) + hash(length - 1)

  def bearer: String =
    bearer(TOKEN_LENGTH)

  def bearer(tokenLength: Int): String =
    if (tokenLength == 0) ""
    else TOKEN_CHARS(secureRandom.nextInt(TOKEN_CHARS.length())) + bearer(tokenLength - 1)
}