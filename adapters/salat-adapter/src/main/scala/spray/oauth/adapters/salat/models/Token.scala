package spray.oauth.adapters.salat.models

import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import com.github.nscala_time.time.Imports._
import com.novus.salat.EnumStrategy
import spray.oauth.adapters.salat.utils._
import org.joda.time.{ PeriodType, ReadablePeriod, ReadableDuration }
import spray.oauth.models.GrantType
import spray.oauth.utils.{ OAuth2Utils, TokenGenerator }
import spray.oauth.{ AuthUser, AuthInfo, AccessToken }
import spray.oauth.utils.OAuth2Utils._
import spray.oauth.utils.OAuth2Parameters._
import com.mongodb.casbah.commons.TypeImports.ObjectId

/**
 * Created with IntelliJ IDEA.
 * User: hasan.ozgan
 * Date: 3/7/14
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
case class Token(@Key("_id") id: ObjectId = new ObjectId,
    @Key("_fk_consumer") fk_consumer: ObjectId,
    @Key("_fk_user") fk_user: Option[ObjectId],
    scopes: List[String],
    token: String,
    refresh_token: Option[String],
    redirect_uri: Option[String],
    ip_restriction: Option[String],
    grant_type: String,
    token_type: TokenType.Value = TokenType.Bearer,
    created_on: DateTime = DateTime.now,
    updated_on: DateTime = DateTime.now,
    expired_on: DateTime) {

  def expires_in = {
    if (expired_on >= DateTime.now) {
      val interval: Interval = new Interval(DateTime.now, expired_on)
      interval.toPeriod(PeriodType.seconds()).getSeconds
    } else 0
  }

  def toScopeString: Option[String] = {
    if (!scopes.isEmpty) Some(scopes.mkString(" ")) else None
  }

  def toAccessToken: AccessToken = {
    AccessToken(token, refresh_token, TokenType.Bearer.toString, toScopeString, expires_in, updated_on.toDate)
  }

  def toAuthInfo: AuthInfo = {
    val clientId = Some(fk_consumer.toString)
    val refreshable = refresh_token.isDefined
    AuthInfo(fk_user.map(x => AuthUser(x.toString)), clientId, toScopeString, redirect_uri, refreshable, GrantType.convertFromString(grant_type), ip_restriction)
  }
}

object TokenDAO extends BaseDAO[Token]("tokens") {

  def findByAccessToken(access_token: String): Option[Token] = {
    findOne(MongoDBObject("token" -> access_token))
  }

  def findConsumerToken(clientId: String) = {
    findOne(MongoDBObject("_fk_consumer" -> new ObjectId(clientId), "_fk_user" -> MongoDBObject("$exists" -> "false"), "grant_type" -> GrantType.ClientCredentials.toString)) map { x => x.toAuthInfo }
  }

  def findUserToken(clientId: String, user: AuthUser, grantType: GrantType.Value): Option[AuthInfo] = {
    findOne(MongoDBObject("_fk_consumer" -> new ObjectId(clientId), "_fk_user" -> new ObjectId(user.id), "grant_type" -> grantType.toString)) map { x => x.toAuthInfo }
  }

  def getCurrentAccessToken(access_token: String): Option[Token] = {
    findByAccessToken(access_token).filter(x => x.expires_in > 0)
  }

  def findAuthInfoByAccessToken(access_token: String): Option[AuthInfo] = {
    getCurrentAccessToken(access_token) map { x => x.toAuthInfo }
  }

  def findByRefreshToken(refresh_token: String): Option[Token] = {
    findOne(MongoDBObject("refresh_token" -> refresh_token))
  }

  def findAuthInfoByRefreshToken(refresh_token: String): Option[AuthInfo] = {
    findByRefreshToken(refresh_token) map { x => x.toAuthInfo }
  }

  def findByAuthInfo(info: AuthInfo): Option[Token] = {
    try {
      val clientId = info.clientId.map { c => new ObjectId(c) }
      val userId = info.user.map(x => new ObjectId(x.id))

      findOne(MongoDBObject("_fk_consumer" -> clientId, "_fk_user" -> userId, "grant_type" -> info.grantType.toString))
    } catch {
      case ex: Exception => None
    }
  }

  def createToken(info: AuthInfo): Option[Token] = {
    findByAuthInfo(info) match {
      case Some(token) => renewToken(info, token)
      case None => newToken(info)
    }
  }

  def newToken(info: AuthInfo): Option[Token] = {
    val clientId = info.clientId.map { x => new ObjectId(x) }.getOrElse(throw new Exception("ClientId Not Found"))
    val access_token = TokenGenerator.bearer(TOKEN_LENGTH)
    val refresh_token: Option[String] = if (info.refreshable && info.remoteAddress.isEmpty) Some(TokenGenerator.bearer(REFRESH_TOKEN_LENGTH)) else None
    val created_on = DateTime.now
    val expired_on = created_on + TOKEN_DURATION
    var token = Token(new ObjectId, clientId, info.user.map(x => new ObjectId(x.id)), toScopeList(info.scope), access_token, refresh_token, info.redirectUri, info.remoteAddress, info.grantType.toString, TokenType.Bearer, created_on, created_on, expired_on)

    try {
      this.save(token)
      Some(token)
    } catch {
      case e: Exception => None
    }
  }

  def renewToken(info: AuthInfo, token: Token): Option[Token] = {
    val updated_on = DateTime.now
    val expired_on = updated_on + TOKEN_DURATION
    val access_token = TokenGenerator.bearer(TOKEN_LENGTH)
    val refresh_token: Option[String] = if (info.refreshable && info.remoteAddress.isEmpty) token.refresh_token else None

    val new_token = Token(token.id, token.fk_consumer, token.fk_user, toScopeList(info.scope), access_token, refresh_token, token.redirect_uri, info.remoteAddress, info.grantType.toString, token.token_type, token.created_on, updated_on, expired_on)

    try {
      this.save(new_token)
      Some(new_token)
    } catch {
      case e: Exception => {
        println(e)
        None
      }
    }
  }
}

@EnumAs(strategy = EnumStrategy.BY_VALUE)
object TokenType extends Enumeration {
  val Bearer = Value("Bearer")
}
