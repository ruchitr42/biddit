package models

import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp


class Users(tag: Tag) extends Table[User](tag, "users"){
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username")
  def email = column[String]("email")
  def passwordHash = column[String]("password_hash")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")
  def * = (id, username, email, passwordHash, createdAt, updatedAt) <> (User.tupled, User.unapply)
}

object Users {
  val table = TableQuery[Users]
}