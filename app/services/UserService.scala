package services

import models.{User, Users}
import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp
import scala.concurrent.Future
import play.api.db.slick.DatabaseConfigProvider
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class UserService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val db = dbConfigProvider.get.db

  def createUser(username: String, email: String, password: String): Future[Long] = {
    val now = new Timestamp(System.currentTimeMillis())
    val user = User(0, username, email, password, now, now) // Store plain-text password
    val action = Users.query returning Users.query.map(_.id) += user
    db.run(action)
  }

  def login(email: String, password: String): Future[Option[Long]] = {
    val action = Users.query.filter(_.email === email).filter(_.passwordHash === password).result.headOption
    db.run(action).map(_.map(_.id)) // Return user ID if email and password match
  }
}