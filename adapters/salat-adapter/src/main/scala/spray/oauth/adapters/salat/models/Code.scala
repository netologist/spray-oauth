package spray.oauth.adapters.salat.models

import com.novus.salat.global._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import com.github.nscala_time.time.Imports._
import org.joda.time.PeriodType
import spray.oauth.models.GrantType
import spray.oauth.utils.OAuth2Parameters._
import spray.oauth.utils.OAuth2Utils._
import spray.oauth.utils.TokenGenerator
import spray.oauth.adapters.salat.utils.BaseDAO
import spray.oauth.{ AuthUser, AuthInfo }

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 4/21/14
 * Time: 9:44 AM
 * To change this template use File | Settings | File Templates.
 */

case class Code(@Key("_id") id: ObjectId = new ObjectId,
    @Key("_fk_consumer") fk_consumer: ObjectId,
    @Key("_fk_user") fk_user: Option[ObjectId],
    scope: Option[String],
    code: String,
    token_refreshable: Boolean,
    redirect_uri: Option[String],
    ip_restriction: Option[String],
    created_on: DateTime = DateTime.now,
    deleted_on: DateTime = DateTime.now,
    expired_on: DateTime) {

  def expires_in = {
    if (expired_on >= DateTime.now) {
      val interval: Interval = new Interval(DateTime.now, expired_on)
      interval.toPeriod(PeriodType.seconds()).getSeconds
    } else 0
  }

  def toAuthInfo: AuthInfo = {
    val clientId = Some(fk_consumer.toString)
    AuthInfo(fk_user.map(x => AuthUser(x.toString)), clientId, scope, redirect_uri, token_refreshable, GrantType.AuthorizationCode, ip_restriction)
  }

}

object CodeDAO extends BaseDAO[Code]("codes") {
  def findAuthInfoByCode(code: String): Option[AuthInfo] = {
    findOne(MongoDBObject("code" -> code)).filter(x => x.expires_in > 0).map { x => x.toAuthInfo }
  }

  def deleteCode(code: String): Unit = {
    remove(MongoDBObject("code" -> code))
  }

  def createCode(info: AuthInfo): Option[Code] = {
    val clientId = info.clientId.map { x => new ObjectId(x) }.getOrElse(throw new Exception("ClientId Not Found"))
    val created_on = DateTime.now
    val expired_on = created_on + CODE_DURATION
    var code = Code(new ObjectId, clientId, info.user.map(x => new ObjectId(x.id)), info.scope, TokenGenerator.bearer(CODE_LENGTH), info.refreshable, info.redirectUri, info.remoteAddress, created_on, created_on, expired_on)

    try {
      this.save(code)
      Some(code)
    } catch {
      case e: Exception => None
    }
  }
}

