package services

import models.{Bookmark, Bookmarks}
import slick.jdbc.PostgresProfile.api._

import java.sql.Timestamp
import scala.concurrent.Future
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class BookmarkService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val db = dbConfigProvider.get.db

  def bookmarkPost(postId: Long, userId: Long): Future[Unit] = {
    val now = new Timestamp(System.currentTimeMillis())
    val bookmark = Bookmark(postId, userId, now)
    val action = Bookmarks.query += bookmark
    db.run(action).map(_ => ())
  }

  def unbookmarkPost(postId: Long, userId: Long): Future[Unit] = {
    val action = Bookmarks.query.filter(b => b.postId === postId && b.userId === userId).delete
    db.run(action).map(_ => ())
  }

  def getBookmarks(userId: Long): Future[Seq[Long]] = {
    db.run(Bookmarks.query.filter(_.userId === userId).map(_.postId).result)
  }
}