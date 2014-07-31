package spray.oauth.adapters.inmemory.models

import com.github.nscala_time.time.Imports._
import org.joda.time.PeriodType
import spray.oauth.adapters.inmemory.utils.{ Entity, DAO, Sequence }
import spray.oauth.models.GrantType
import spray.oauth.utils.OAuth2Parameters._
import spray.oauth.utils.TokenGenerator
import spray.oauth.{ AuthUser, AuthInfo }

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 4/21/14
 * Time: 9:44 AM
 * To change this template use File | Settings | File Templates.
 */

case class Code(
    id: Long,
    fk_consumer: Long,
    fk_user: Option[Long],
    scope: Option[String],
    code: String,
    token_refreshable: Boolean,
    redirect_uri: Option[String],
    ip_restriction: Option[String],
    created_on: DateTime = DateTime.now,
    deleted_on: DateTime = DateTime.now,
    expired_on: DateTime) extends Entity(id) {

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

object CodeDAO extends DAO[Code] {
  def findAuthInfoByCode(code: String): Option[AuthInfo] = {
    findOneByCode(code).filter(x => x.expires_in > 0).map { x => x.toAuthInfo }
  }

  def findOneByCode(code: String): Option[Code] = {
    findBy(p => p.code.equals(code))
  }

  def deleteCode(code: String): Unit = {
    val found = findBy(p => p.code.equals(code))
    if (found.nonEmpty) {
      remove(found.get)
    }
  }

  def createCode(info: AuthInfo): Option[Code] = {
    val clientId = info.clientId.map { x => x.toLong }.getOrElse(throw new Exception("ClientId Not Found"))
    val created_on = DateTime.now

    val expired_on = created_on + CODE_DURATION
    var code = Code(Sequence.nextId, clientId, info.user.map(x => x.id.toLong), info.scope, TokenGenerator.bearer(CODE_LENGTH), info.refreshable, info.redirectUri, info.remoteAddress, created_on, created_on, expired_on)

    try {
      this.save(code)
      Some(code)
    } catch {
      case e: Exception => None
    }
  }
}

