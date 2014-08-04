package spray.oauth.adapters.salat

import com.mongodb.WriteConcern
import com.novus.salat._
import com.mongodb.casbah.Imports._
import spray.oauth.AuthUser

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 6/3/14
 * Time: 9:02 AM
 * To change this template use File | Settings | File Templates.
 */
package object utils {

  com.mongodb.casbah.commons.conversions.scala.RegisterConversionHelpers()
  com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers()

  implicit val wc: WriteConcern = WriteConcern.SAFE

  implicit val ctx = new Context {
    val name = "When-Necessary-TypeHint-Context"
    override val typeHintStrategy = StringTypeHintStrategy(when = TypeHintFrequency.WhenNecessary, typeHint = TypeHint)
  }

}