package services

import models.{Like, Likes, Posts}
import slick.jdbc.PostgresProfile.api._

import java.sql.Timestamp
import scala.concurrent.Future
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class LikeService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val db = dbConfigProvider.get.db


  def likePost(postId: Long, userId: Long): Future[Unit] = {
    val now = new Timestamp(System.currentTimeMillis())
    val like = Like(postId, userId, now)
    val action = Likes.query += like
    db.run(action).map(_ => ())
  }

  def unlikePost(postId: Long, userId: Long): Future[Unit] = {
    val action = Likes.query.filter(l => l.postId === postId && l.userId === userId).delete
    db.run(action).map(_ => ())
  }

  def getLikeCount(postId: Long): Future[Int] = {
    db.run(Likes.query.filter(_.postId === postId).length.result)
  }

  def getLikers(postId: Long, requestingUserId: Long): Future[Seq[Long]] = {
    val action = for {
      post <- Posts.query.filter(_.id === postId).result.headOption
      likers <- if (post.exists(_.userId == requestingUserId)) {
        Likes.query.filter(_.postId === postId).map(_.userId).result
      } else {
        DBIO.successful(Seq.empty[Long])
      }
    } yield likers
    db.run(action)
  }
}