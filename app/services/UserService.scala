package services

import models.{User, Users}
import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp
import scala.concurrent.Future
import play.api.db.slick.DatabaseConfigProvider
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

class UserService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val db = dbConfigProvider.get.db

  def createUser(username: String, email: String, passwordHash: String): Future[Long] = {
    val now = new Timestamp(System.currentTimeMillis())
    val user = User(0, username, email, passwordHash, now, now)
    val action = Users.query returning Users.query.map(_.id) += user
    db.run(action)
  }

  def login(email: String, password: String): Future[Option[String]] = {
    val action = Users.query.filter(_.email === email).result.headOption
    db.run(action).map{ maybeUser =>
      maybeUser.flatMap { user =>
        if(user.passwordHash == password){
          Some(generateToken(user.id))
        }
        else{
          None
        }
      }
    }
  }

  private def generateToken(userId: Double): String = {
    val algorithm = Algorithm.HMAC256("secret") // Secure this in production
    JWT.create().withClaim("userId", userId: Double).sign(algorithm)
  }
}