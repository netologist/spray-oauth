package com.hasanozgan.demo.inmemory.oauth2.routes

import spray.oauth.adapters.inmemory.SprayOAuth2Support
import spray.oauth.authentication.ResourceAuthenticator
import spray.routing.HttpService
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by hasanozgan on 31/07/14.
 */
trait ApiRoutes extends SprayOAuth2Support with ResourceAuthenticator with HttpService {
  val apiRoutes =
    path("user") {
      get {
        authenticate(tokenAuthenticator) { info =>
          authorize(allowedScopes(info, "membership", "membership.readonly")) {
            complete("Success")
          }
        }
      }
    }
}