package org.netology.spray.rest.oauth2.utils

import spray.routing.{ Rejection, AuthenticationFailedRejection }
import spray.http.{ Uri, HttpHeader }

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 4/7/14
 * Time: 5:11 PM
 * To change this template use File | Settings | File Templates.
 */
case class SecurePageRejection(uri: Uri) extends Rejection
