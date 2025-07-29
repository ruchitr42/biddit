package models

import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp

class Posts(tag: Tag) extends Table[Post](tag, "posts") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Long]("user_id")
  def title = column[String]("title")
  def content = column[String]("content")
  def status = column[String]("status")
  def postType = column[String]("type")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")
  def publishedAt = column[Option[Timestamp]]("published_at")
  def * = (id, userId, title, content, status, postType, createdAt, updatedAt, publishedAt) <> ((Post.apply _).tupled, Post.unapply)
}

object Posts {
  val query = TableQuery[Posts]
}