package spray.oauth.endpoints

/**
 * Created by hasanozgan on 06/06/14.
 */
trait OAuth2Services extends TokenService with AuthorizeService {

  val defaultOAuth2Routes = defaultTokenRoutes ~ defaultAuthorizeRoutes

}
