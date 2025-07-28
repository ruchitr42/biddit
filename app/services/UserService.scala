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

  def createUser(username: String, email: String, passwordHash: String): Future[Long] = {
    val now = new Timestamp(System.currentTimeMillis())
    val user = User(0, username, email, passwordHash, now, now)
    val action = Users.query returning Users.query.map(_.id) += user
    db.run(action)
  }
}