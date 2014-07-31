package org.netology.spray.rest.oauth2.utils

import spray.routing._
import spray.http._
import java.net.URLEncoder

trait CustomRejectionHandler extends HttpService {
  implicit val myRejectionHandler = RejectionHandler {
    case SecurePageRejection(uri) :: _ => redirectWithUri(uri)
    //case _ => complete(StatusCodes.BadRequest, "Something went wrong here")
  }

  def redirectWithUri(uri: Uri): StandardRoute = {
    val encodedUri = URLEncoder.encode(uri.toString(), "UTF-8")
    redirect(s"https://localhost/oauth2/login?continue=${encodedUri}", StatusCodes.TemporaryRedirect)
  }
}