package services

import models.{User, Users}
import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp
import scala.concurrent.Future
import play.api.db.slick.DatabaseConfigProvider
import play.api.Configuration
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.mindrot.jbcrypt.BCrypt

class UserService @Inject()(dbConfigProvider: DatabaseConfigProvider, config: Configuration)(implicit ec: ExecutionContext) {
  val db = dbConfigProvider.get.db
  private val jwtSecret = config.get[String]("jwt.secret")

  def createUser(username: String, email: String, password: String): Future[Long] = {
    val now = new Timestamp(System.currentTimeMillis())
    val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
    val user = User(0, username, email, passwordHash, now, now)
    val action = Users.query returning Users.query.map(_.id) += user
    db.run(action)
  }

  def login(email: String, password: String): Future[Option[String]] = {
    val action = Users.query.filter(_.email === email).result.headOption
    db.run(action).map { maybeUser =>
      maybeUser.flatMap { user =>
        if (BCrypt.checkpw(password, user.passwordHash)) {
          Some(generateToken(user.id))
        } else {
          None
        }
      }
    }
  }

  private def generateToken(userId: Long): String = {
    val algorithm = Algorithm.HMAC256(jwtSecret)
    JWT.create()
      .withClaim("userId", userId.toString)
      .withIssuedAt(new java.util.Date())
      .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 hours
      .sign(algorithm)
  }
}