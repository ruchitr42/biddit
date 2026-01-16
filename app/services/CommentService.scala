package services

import models.{Comment, Comments}
import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp
import scala.concurrent.Future
import play.api.db.slick.DatabaseConfigProvider
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CommentService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val db = dbConfigProvider.get.db

  def addComment(postId: Long, userId: Long, content: String): Future[Long] = {
    val now = new Timestamp(System.currentTimeMillis())
    val comment = Comment(0, postId, userId, content, now, now)
    val action = Comments.query returning Comments.query.map(_.id) += comment
    db.run(action)
  }

  def getComments(postId: Long): Future[Seq[Comment]] = {
    db.run(Comments.query.filter(_.postId === postId).result)
  }
}