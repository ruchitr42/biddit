package models

import java.sql.Timestamp

case class Bid(
                id: Long,
                postId: Long,
                userId: Long,
                price: BigDecimal,
                quantity: Int,
                createdAt: Timestamp
              )
