package spray.oauth.endpoints

import spray.json.DefaultJsonProtocol

import spray.routing.HttpService
import spray.oauth.directives.OAuth2Directives
import spray.oauth.models.TokenRequest
import spray.http.StatusCodes
import spray.oauth.models.TokenResponse.{ Token, Error, Code }
import spray.httpx.SprayJsonSupport

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val TokenFormat = jsonFormat5(Token)
  implicit val CodeFormat = jsonFormat1(Code)
  implicit val ErrorFormat = jsonFormat2(Error)
}

import MyJsonProtocol._

/**
 * Created by hasanozgan on 03/06/14.
 */
trait TokenService extends HttpService with SprayJsonSupport with OAuth2Directives {

  val defaultTokenRoutes =

    path("token") {

      fetchTokenRequest { request: TokenRequest =>
        grantHandler(request) {
          case error: Error => complete(error)
          case token: Token => complete(token)
          case code: Code => complete(code)
        }
      }
    } ~
      path("revoke") {
        get {
          complete {
            "TODO"
          }
        }
      }
}