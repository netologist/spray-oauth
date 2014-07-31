package spray.oauth.utils

import java.security.MessageDigest

/**
 * Created by hasanozgan on 07/07/14.
 */
object HashUtils {
  def md5(s: String) = {
    val md5 = MessageDigest.getInstance("MD5")
    md5.reset()
    md5.update(s.getBytes("UTF-8"))
    md5.digest().map(0xFF & _).map { "%02x".format(_) }.foldLeft("") { _ + _ }
  }
}
