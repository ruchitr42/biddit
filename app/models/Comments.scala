package models

import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp

class Comments(tag: Tag) extends Table[Comment](tag, "comments") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def postId = column[Long]("post_id")
  def userId = column[Long]("user_id")
  def content = column[String]("content")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")
  def * = (id, postId, userId, content, createdAt, updatedAt) <> ((Comment.apply _).tupled, Comment.unapply)
  def post = foreignKey("fk_comments_post", postId, Posts.query)(_.id)
  def user = foreignKey("fk_comments_user", userId, Users.query)(_.id)
}

object Comments {
  val query = TableQuery[Comments]
}