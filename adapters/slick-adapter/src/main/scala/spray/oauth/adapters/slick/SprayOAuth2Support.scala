package spray.oauth.adapters.slick

import spray.oauth.utils.DefaultGrantHandler
import spray.oauth.{ OAuth2DataHandler, OAuth2GrantHandler }

/**
 * Created by hasanozgan on 04/08/14.
 */
trait SprayOAuth2Support {

  implicit def grantHandler: OAuth2GrantHandler = DefaultGrantHandler

  implicit def dataHandler: OAuth2DataHandler = SlickDataHandler

}
