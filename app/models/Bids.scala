package models
import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp
class Bids(tag: Tag) extends Table[Bid](tag, "bids") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def postId = column[Long]("post_id")
  def userId = column[Long]("user_id")
  def price = column[BigDecimal]("price")
  def quantity = column[Int]("quantity")
  def createdAt = column[Timestamp]("created_at")
  def * = (id, postId, userId, price, quantity, createdAt) <> (Bid.tupled, Bid.unapply)
  def post = foreignKey("post_fk", postId, Posts.query)(_.id)
  def user = foreignKey("user_fk", userId, Users.query)(_.id)
}

object Bids {
  val query = TableQuery[Bids]
}
