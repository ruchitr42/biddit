//package services
//
//import models.{Bid, Bids, Post, Posts, ProductDetail, ProductDetails, Users}
//import slick.jdbc.PostgresProfile.api._
//import java.sql.Timestamp
//import scala.concurrent.Future
//import play.api.db.slick.DatabaseConfigProvider
//import javax.inject.Inject
//import scala.concurrent.ExecutionContext
//import play.api.Logger
//
//case class BidInput(
//                     price: BigDecimal,
//                     quantity: Int
//                   )
//
//class BidService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
//  val db = dbConfigProvider.get.db
//  private val logger = Logger(this.getClass)
//
//  def createBid(postId: Long, userId: Long, bidInput: BidInput): Future[Either[String, Long]] = {
//    val now = new Timestamp(System.currentTimeMillis())
//    logger.info(s"Starting createBid for postId: $postId, userId: $userId, price: ${bidInput.price}, quantity: ${bidInput.quantity}")
//
//    val action = for {
//      // Check if user exists
//      userExists <- Users.query.filter(_.id === userId).exists.result
//      _ = logger.debug(s"User existence check: userId=$userId, exists=$userExists")
//      // Check if post exists and is a product post
//      bidResult <- if (!userExists) {
//        logger.warn(s"User not found for userId: $userId")
//        DBIO.successful(Left[String, Long]("User not found"))
//      } else {
//        logger.debug(s"Querying for product post with postId: $postId")
//        Posts.query.filter(_.id === postId).filter(_.postType === "product").result.headOption.flatMap {
//          case None =>
//            logger.warn(s"Product post not found for postId: $postId")
//            DBIO.successful(Left[String, Long]("Product post not found"))
//          case Some(p) =>
//            logger.debug(s"Found post: $p")
//            // Check if bidding is still open
//            logger.debug(s"Querying product details for postId: $postId")
//            ProductDetails.query.filter(_.postId === postId).result.headOption.flatMap {
//              case Some(pd) =>
//                logger.debug(s"Found product details: $pd")
//                val createdAt = p.createdAt.getTime
//                val durationMillis = (pd.bidDurationHours * 3600 + pd.bidDurationMinutes * 60) * 1000
//                val endTime = createdAt + durationMillis
//                val currentTime = System.currentTimeMillis()
//                logger.debug(s"Bid time check: createdAt=$createdAt, durationMillis=$durationMillis, endTime=$endTime, currentTime=$currentTime")
//                if (endTime < currentTime) {
//                  logger.warn(s"Bidding closed for postId: $postId")
//                  DBIO.successful(Left[String, Long]("Bidding is closed"))
//                } else {
//                  // Validate bid
//                  logger.debug(s"Validating bid: price=${bidInput.price}, quantity=${bidInput.quantity}, minimumBid=${pd.minimumBid}, maxQuantityPerUser=${pd.maxQuantityPerUser}, availableQuantity=${pd.quantity}")
//                  if (bidInput.price < pd.minimumBid) {
//                    logger.warn(s"Bid price ${bidInput.price} is less than minimum bid ${pd.minimumBid}")
//                    DBIO.successful(Left[String, Long](s"Bid price must be at least ${pd.minimumBid}"))
//                  } else if (bidInput.quantity > pd.maxQuantityPerUser) {
//                    logger.warn(s"Bid quantity ${bidInput.quantity} exceeds max quantity per user ${pd.maxQuantityPerUser}")
//                    DBIO.successful(Left[String, Long](s"Bid quantity cannot exceed ${pd.maxQuantityPerUser}"))
//                  } else if (bidInput.quantity > pd.quantity) {
//                    logger.warn(s"Bid quantity ${bidInput.quantity} exceeds available quantity ${pd.quantity}")
//                    DBIO.successful(Left[String, Long](s"Bid quantity cannot exceed available ${pd.quantity}"))
//                  } else if (bidInput.quantity <= 0) {
//                    logger.warn(s"Invalid bid quantity: ${bidInput.quantity}")
//                    DBIO.successful(Left[String, Long]("Bid quantity must be positive"))
//                  } else if (bidInput.price <= BigDecimal(0)) {
//                    logger.warn(s"Invalid bid price: ${bidInput.price}")
//                    DBIO.successful(Left[String, Long]("Bid price must be positive"))
//                  } else {
//                    // Insert bid
//                    val bid = Bid(0, postId, userId, bidInput.price, bidInput.quantity, now)
//                    logger.debug(s"Inserting bid: $bid")
//                    (Bids.query returning Bids.query.map(_.id) += bid)
//                      .map { bidId =>
//                        logger.info(s"Successfully inserted bid with ID: $bidId")
//                        Right[String, Long](bidId)
//                      }
//                  }
//                }
//              case None =>
//                logger.warn(s"Product details not found for postId: $postId")
//                DBIO.successful(Left[String, Long]("Product details not found"))
//            }
//        }
//      }
//    } yield bidResult
//
//    logger.debug(s"Executing database action for createBid")
//    db.run(action.transactionally).map { result =>
//      logger.debug(s"Database action result: $result")
//      result
//    }.recover {
//      case e: Exception =>
//        logger.error(s"Database error in createBid: ${e.getMessage}", e)
//        Left(s"Database error: ${e.getMessage}")
//    }
//  }
//}