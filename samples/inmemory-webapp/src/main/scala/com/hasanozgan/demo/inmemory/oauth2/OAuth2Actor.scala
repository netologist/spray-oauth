package org.netology.spray.rest.oauth2

import com.hasanozgan.demo.inmemory.oauth2.routes.ApiRoutes
import org.netology.spray.rest.oauth2.routes._
import org.netology.spray.rest.oauth2.utils.CustomRejectionHandler
import akka.actor.Actor
import spray.oauth.adapters.inmemory.SprayOAuth2Support
import spray.oauth.endpoints.OAuth2Services
import spray.routing._
import spray.http.StatusCodes._
import spray.routing.Directive.pimpApply
import spray.util.LoggingContext
import spray.http.HttpRequest
import spray.http.HttpResponse
import spray.http.Timedout

/**
 * Created by hasanozgan on 01/03/14.
 */
class OAuth2Actor extends Actor with SprayOAuth2Support with OAuth2Services with IndexRoutes with ApiRoutes with CustomRejectionHandler {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = handleTimeouts orElse runRoute(handleRejections(myRejectionHandler)(handleExceptions(myExceptionHandler)(defaultOAuth2Routes ~ apiRoutes ~ initRoutes)))

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

class SomeCustomException(msg: String) extends RuntimeException(msg)

