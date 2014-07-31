package spray.oauth.rejections

import spray.routing.Rejection

/**
 * Created by hasanozgan on 16/05/14.
 */
case class AuthHandlerErrorRejection(error: String) extends Rejection
