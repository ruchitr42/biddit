package models

import play.api.libs.json._

case class ProductDetail(
                          postId: Long,
                          productDetails: String,
                          minimumBid: BigDecimal,
                          quantity: Int,
                          maxQuantityPerUser: Int,
                          bidDurationHours: Int,
                          bidDurationMinutes: Int
                        )

object ProductDetail {
  implicit val format: Format[ProductDetail] = Json.format[ProductDetail]
}