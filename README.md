Spray OAuth2 Provider Library
===========

Spray OAuth2 Provider Library

[![Build Status](https://api.travis-ci.org/hasanozgan/spray-oauth.svg?branch=master)](https://travis-ci.org/hasanozgan/spray-oauth)

##### Road Map 
 - Scala 2.11 migration
 - Write tests
 - Spray OAuth Rejectives
 - Write documentation
 - Salat Demo with Twirl Support
 - Slick Adapter

##### SBT

```scala
resolvers += "Spray OAuth repo" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "com.hasanozgan"       %%  "spray-oauth"                  % "1.0-SNAPSHOT",
  "com.hasanozgan"       %%  "spray-oauth-salat-plugin"     % "1.0-SNAPSHOT"
)
```

##### Actor Support
 - SprayOAuth2Support for adapter support (salat, slick and in-memory)
 - OAuth2Services for defaultOAuth2Routes

```scala
class OAuth2Actor extends Actor with SprayOAuth2Support with OAuth2Services with IndexRoutes with CustomRejectionHandler {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = handleTimeouts orElse runRoute(handleRejections(myRejectionHandler)(handleExceptions(myExceptionHandler)(defaultOAuth2Routes ~ initRoutes)))

  def handleTimeouts: Receive = {
    case Timedout(x: HttpRequest) =>
      sender ! HttpResponse(InternalServerError, "Something is taking way too long.")
  }

  implicit def myExceptionHandler(implicit log: LoggingContext) =
    ExceptionHandler.apply {
      case e: Exception => {
        complete(InternalServerError, e.getMessage)
      }

    }
}
```

##### Default Route Services

###### Token Route Service
```scala
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
    }
}
```

###### Auth Route Service
```scala
package spray.oauth.endpoints

import spray.routing.{ RequestContext, HttpService }
import spray.oauth.directives.OAuth2Directives
import spray.http.{ HttpCredentials, HttpHeader, HttpRequest, StatusCodes }
import spray.oauth.models.AuthResponse.{ Error, Redirect, Approval }
import spray.routing.authentication._
import scala.concurrent.{ ExecutionContext, Future }
import ExecutionContext.Implicits.global
import spray.routing.authentication.UserPass
import spray.oauth.models.{ ApprovalForm, AuthResponse, AuthRequest }
import spray.oauth.{ AuthUser, AuthInfo }
import spray.oauth.utils.OAuth2Parameters._
import spray.oauth.utils.OAuth2Utils
import spray.http._
import MediaTypes._

/**
 * Created by hasanozgan on 03/06/14.
 */
trait AuthorizeService extends HttpService with OAuth2Directives {

  def myUserPassAuthenticator(userPass: Option[UserPass]): Future[Option[AuthUser]] = {
    Future {
      if (userPass.isDefined)
        dataHandler.getUser(userPass.get.user, userPass.get.pass)
      else None
    }
  }

  val defaultAuthorizeRoutes =
    path("authorize") {
      authenticate(BasicAuth(myUserPassAuthenticator _, realm = "secure site")) { user =>
        fetchAuthRequest(user) { request =>
          authHandler(request) {
            case AuthResponse.Approval(form) => respondWithMediaType(`text/html`) {
              complete(renderApprovalForm(form))
            }
            case AuthResponse.Redirect(uri) => redirect(uri, StatusCodes.TemporaryRedirect)
            case AuthResponse.Error(err, desc) => complete(s"ERROR: ${err}") //render("tpl/approvalPage", request)
          }
        }
      }
    } 
  }
}
```

###### Rest API Authenticator Support

```scala
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
``` 
