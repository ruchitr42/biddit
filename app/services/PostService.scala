package services

import models.{Post, Posts, ProductDetail, ProductDetails, Users}
import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp
import scala.concurrent.Future
import play.api.db.slick.DatabaseConfigProvider
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import play.api.Logger

case class ProductDetailInput(
                               productDetails: String,
                               minimumBid: BigDecimal,
                               quantity: Int,
                               maxQuantityPerUser: Int,
                               bidDurationHours: Int,
                               bidDurationMinutes: Int
                             )

case class PostWithDetails(
                            post: Post,
                            productDetails: Option[ProductDetail],
                            remainingBidTime: Option[RemainingBidTime]
                          )

case class RemainingBidTime(hours: Int, minutes: Int)

class PostService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val db = dbConfigProvider.get.db
  private val logger = Logger(this.getClass)

  def createPost(userId: Long, title: String, content: String, status: String, postType: String, productDetails: Option[ProductDetailInput]): Future[Either[String, Long]] = {
    val now = new Timestamp(System.currentTimeMillis())
    val post = Post(0, userId, title, content, status, postType, now, now, if (status == "published") Some(now) else None)
    logger.info(s"Creating post for userId: $userId, postType: $postType, title: $title")

    val action = for {
      userExists <- Users.query.filter(_.id === userId).exists.result
      _ = logger.debug(s"User exists: $userExists")
      result <- if (userExists) {
        for {
          postId <- Posts.query returning Posts.query.map(_.id) += post
          _ = logger.debug(s"Inserted post with ID: $postId")
          _ <- productDetails match {
            case Some(details) if postType == "product" =>
              logger.debug(s"Inserting product details for postId: $postId")
              ProductDetails.query += ProductDetail(
                postId,
                details.productDetails,
                details.minimumBid,
                details.quantity,
                details.maxQuantityPerUser,
                details.bidDurationHours,
                details.bidDurationMinutes
              )
            case _ => DBIO.successful(())
          }
        } yield Right[String, Long](postId)
      } else {
        logger.warn(s"User not found for userId: $userId")
        DBIO.successful(Left[String, Long]("User not found"))
      }
    } yield result

    db.run(action.transactionally).recover {
      case e: Exception =>
        logger.error(s"Failed to create post: ${e.getMessage}", e)
        Left(s"Database error: ${e.getMessage}")
    }
  }

  def getPost(postId: Long): Future[Option[PostWithDetails]] = {
    val action = for {
      post <- Posts.query.filter(_.id === postId).result.headOption
      productDetails <- post match {
        case Some(p) if p.`postType` == "product" =>
          ProductDetails.query.filter(_.postId === postId).result.headOption
        case _ => DBIO.successful(None)
      }
    } yield post.map { p =>
      val remainingBidTime = productDetails.map { pd =>
        val createdAt = p.createdAt.getTime
        val durationMillis = (pd.bidDurationHours * 3600 + pd.bidDurationMinutes * 60) * 1000
        val endTime = createdAt + durationMillis
        val remainingMillis = endTime - System.currentTimeMillis()
        if (remainingMillis <= 0) {
          RemainingBidTime(0, 0)
        } else {
          val hours = (remainingMillis / (1000 * 3600)).toInt
          val minutes = ((remainingMillis % (1000 * 3600)) / (1000 * 60)).toInt
          RemainingBidTime(hours, minutes)
        }
      }
      PostWithDetails(p, productDetails, remainingBidTime)
    }
    db.run(action)
  }
}