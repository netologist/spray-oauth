package spray.oauth.rejections

import spray.routing.Rejection
import spray.http.Uri

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 4/7/14
 * Time: 5:11 PM
 * To change this template use File | Settings | File Templates.
 */
case class SecurePageRejection(uri: Uri) extends Rejection
