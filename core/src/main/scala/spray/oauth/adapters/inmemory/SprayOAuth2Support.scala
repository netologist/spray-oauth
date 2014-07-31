package spray.oauth.adapters.inmemory

import spray.oauth.{ OAuth2GrantHandler, OAuth2DataHandler }
import spray.oauth.utils.DefaultGrantHandler
import spray.oauth.endpoints.{ OAuth2Services, AuthorizeService, TokenService }

/**
 * Created by hasanozgan on 03/06/14.
 */
trait SprayOAuth2Support {

  implicit def grantHandler: OAuth2GrantHandler = DefaultGrantHandler

  implicit def dataHandler: OAuth2DataHandler = InMemoryDataHandler

}
