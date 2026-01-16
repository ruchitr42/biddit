package models

import slick.jdbc.PostgresProfile.api._

class ProductDetails(tag: Tag) extends Table[ProductDetail](tag, "product_details") {
  def postId = column[Long]("post_id", O.PrimaryKey)
  def productDetails = column[String]("product_details")
  def minimumBid = column[BigDecimal]("minimum_bid")
  def quantity = column[Int]("quantity")
  def maxQuantityPerUser = column[Int]("max_quantity_per_user")
  def bidDurationHours = column[Int]("bid_duration_hours")
  def bidDurationMinutes = column[Int]("bid_duration_minutes")
  def * = (postId, productDetails, minimumBid, quantity, maxQuantityPerUser, bidDurationHours, bidDurationMinutes) <> ((ProductDetail.apply _).tupled, ProductDetail.unapply)
  def post = foreignKey("fk_product_details_post", postId, Posts.query)(_.id)
}

object ProductDetails {
  val query = TableQuery[ProductDetails]
}