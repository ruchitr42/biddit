package models

import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp

class Likes(tag: Tag) extends Table[Like](tag, "likes") {
  def postId = column[Long]("post_id")
  def userId = column[Long]("user_id")
  def createdAt = column[Timestamp]("created_at")
  def * = (postId, userId, createdAt) <> (Like.tupled, Like.unapply)
  def pk = primaryKey("pk_likes", (postId, userId))
  def post = foreignKey("fk_likes_post", postId, Posts.query)(_.id)
  def user = foreignKey("fk_likes_user", userId, Users.query)(_.id)
}

object Likes {
  val query = TableQuery[Likes]
}