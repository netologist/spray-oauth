package org.netology.spray.rest.oauth2.routes

import spray.oauth.adapters.inmemory.models._
import spray.oauth.adapters.inmemory.utils.Sequence
import spray.routing.{ StandardRoute, HttpService }
import spray.http.{ MediaTypes, StatusCodes }
import com.github.nscala_time.time.Imports._
import spray.oauth.utils.TokenGenerator
import spray.oauth.models.GrantType
import spray.oauth.models.GrantType._
import spray.oauth.directives.SessionDirectives
import MediaTypes._

/**
 * Created by hasanozgan on 17/03/14.
 */
trait IndexRoutes extends HttpService with SessionDirectives {

  val initRoutes =
    path("init") {
      get {
        complete {
          val defaultScopes = List[String]("membership", "membership.readonly")
          val defaultGrants = List[String](GrantType.AuthorizationCode, GrantType.ClientCredentials, GrantType.RefreshToken)

          val role = Role(Sequence.nextId, "default", defaultScopes, defaultGrants)
          RoleDAO.insert(role)

          val user = UserDAO.create("123456", "user", "pass")

          val consumer = Consumer(id = Sequence.nextId,
            fk_role = role.id,
            scopes = List("membership.internal"),
            grants = List[String](GrantType.Password),
            name = "activist",
            site_url = Some("http://localhost"), logo = None, description = None,
            callback_url = Some("http://localhost/callback"),
            client_secret = TokenGenerator.bearer)

          ConsumerDAO.insert(consumer).toString

          s"Success ${consumer.id} - ${consumer.client_secret}"
        }

      }
    } ~
      path("logout") {
        get {
          deleteSession {
            redirect("/oauth2", StatusCodes.TemporaryRedirect)
          }
        }
      } ~
      path("login") {
        post {
          parameters('continue ? "") { continue: String =>
            complete {
              s"redirect ${continue}"
            }
          }
        } ~
          get {
            createSession {
              respondWithMediaType(`text/html`) {
                complete {
                  <html>
                    <body>
                      <h1>HELLO SALAT</h1>
                    </body>
                  </html>
                }
              }

              /*
              completeWithTemplate("auth/login") {
                Map(
                  "name" -> "Chris",
                  "value" -> 10000,
                  "taxed_value" -> (10000 - (10000 * 0.18)),
                  "in_ca" -> true
                )
              }*/
            }
          }
      } ~
      pathPrefix("assets") {
        compressResponse() {
          getFromResourceDirectory("webapp")
        }
      }

}
