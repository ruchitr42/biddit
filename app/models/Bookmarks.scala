package models

import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp

class Bookmarks(tag: Tag) extends Table[Bookmark](tag, "bookmarks") {
  def postId = column[Long]("post_id")
  def userId = column[Long]("user_id")
  def createdAt = column[Timestamp]("created_at")
  def * = (postId, userId, createdAt) <> (Bookmark.tupled, Bookmark.unapply)
  def pk = primaryKey("pk_bookmarks", (postId, userId))
  def post = foreignKey("fk_bookmarks_post", postId, Posts.query)(_.id)
  def user = foreignKey("fk_bookmarks_user", userId, Users.query)(_.id)
}

object Bookmarks {
  val query = TableQuery[Bookmarks]
}