package spray.oauth.directives

import spray.routing._
import spray.routing.directives.BasicDirectives._
import scala.Some
import spray.oauth.utils.OAuth2Parameters

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 3/3/14
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */

//http://kufli.blogspot.com/2013/08/sprayio-rest-services-api-versioning.html
trait ResourceDirectives {

  def contentTypeVersion(contentType: String): Directive1[String] =
    extract { ctx =>
      val header = ctx.request.headers.find(_.name == "Content-Type")
      header match {
        //TODO Parse content-type value with 'contentType' parameter
        case Some(head) => head.value
        case _ => "1" //default to 1
      }
    }

  def headerVersion: Directive1[String] =
    extract { ctx =>
      val header = ctx.request.headers.find(_.name == OAuth2Parameters.RESOURCE_HEADERS_VERSIONING)
      header match {
        case Some(head) => head.value
        case _ => "1" //default to 1
      }
    }

}