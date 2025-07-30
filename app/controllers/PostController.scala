package controllers

import play.api.mvc._
import services.{PostService, PostWithDetails, ProductDetailInput, RemainingBidTime}
import play.api.libs.json._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import models.ProductDetail

class PostController @Inject()(postService: PostService, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val productDetailInputFormat: Format[ProductDetailInput] = Json.format[ProductDetailInput]
  implicit val productDetailFormat: Format[ProductDetail] = Json.format[ProductDetail]
  implicit val remainingBidTimeFormat: Format[RemainingBidTime] = Json.format[RemainingBidTime]
  implicit val postWithDetailsFormat: Format[PostWithDetails] = Json.format[PostWithDetails]

  def create(userId: Long) = Action.async(parse.json) { request =>
    val postType = (request.body \ "postType").as[String]
    if (!Seq("blog", "product").contains(postType)) {
      Future.successful(BadRequest(Json.obj("error" -> "Invalid postType, must be 'blog' or 'product'")))
    } else {
      println("this is the er000: " + request.body.toString());

      val title = (request.body \ "title").as[String]
      val content = (request.body \ "content").as[String]
      val status = (request.body \ "status").as[String]
      val productDetails = if (postType == "product") {
//        println("th epro details are: " + (request.body \ "productDetails").as[String])
//        request.body.as[JsObject].validate[ProductDetailInput].asOpt
        (request.body \ "productDetails").validate[ProductDetailInput].asOpt

      } else {
        println("here is the errorooror")
        None
      }
      println("this is the er: " + request.body.toString());
      if (postType == "product" && productDetails.isEmpty) {
//        println("this is the erroroororor" + request.body.toString());
        Future.successful(BadRequest(Json.obj("error" -> "Product details required for product post")))
      } else {
        postService.createPost(userId, title, content, status, postType, productDetails).map {
          case Right(id) => Ok(Json.obj("id" -> id))
          case Left(error) => BadRequest(Json.obj("error" -> error))
        }
      }
    }
  }

  def get(postId: Long) = Action.async { request =>
    postService.getPost(postId).map {
      case Some(postWithDetails) => Ok(Json.toJson(postWithDetails))
      case None => NotFound(Json.obj("error" -> "Post not found"))
    }
  }
}







//
//
//package controllers
//
//import play.api.mvc._
//import services.{BidService, BidInput, PostService, PostWithDetails, ProductDetailInput, RemainingBidTime}
//import play.api.libs.json._
//import javax.inject.Inject
//import scala.concurrent.{ExecutionContext, Future}
//import models.ProductDetail
//import play.api.Logger
//
//class PostController @Inject()(postService: PostService, bidService: BidService, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {
//  private val logger = Logger(this.getClass)
//
//  implicit val productDetailInputFormat: Format[ProductDetailInput] = Json.format[ProductDetailInput]
//  implicit val productDetailFormat: Format[ProductDetail] = Json.format[ProductDetail]
//  implicit val remainingBidTimeFormat: Format[RemainingBidTime] = Json.format[RemainingBidTime]
//  implicit val postWithDetailsFormat: Format[PostWithDetails] = Json.format[PostWithDetails]
//  implicit val bidInputFormat: Format[BidInput] = Json.format[BidInput]
//
//  def create(userId: Long) = Action.async(parse.json) { request =>
//    val postType = (request.body \ "postType").as[String]
//    if (!Seq("blog", "product").contains(postType)) {
//      logger.error(s"Invalid postType: $postType")
//      Future.successful(BadRequest(Json.obj("error" -> "Invalid postType, must be 'blog' or 'product'")))
//    } else {
//      logger.info(s"Request body: ${request.body.toString()}")
//      val title = (request.body \ "title").as[String]
//      val content = (request.body \ "content").as[String]
//      val status = (request.body \ "status").as[String]
//      val productDetails = if (postType == "product") {
//        val result = request.body.as[JsObject].validate[ProductDetailInput]
//        logger.debug(s"Product details validation: $result")
//        result.asOpt
//      } else {
//        None
//      }
//      logger.info(s"Parsed productDetails: $productDetails")
//      if (postType == "product" && productDetails.isEmpty) {
//        logger.error(s"Product details required for product post: ${request.body.toString()}")
//        Future.successful(BadRequest(Json.obj("error" -> "Product details required for product post")))
//      } else {
//        postService.createPost(userId, title, content, status, postType, productDetails).map {
//          case Right(id) => Ok(Json.obj("id" -> id))
//          case Left(error) => BadRequest(Json.obj("error" -> error))
//        }
//      }
//    }
//  }
//
//  def get(postId: Long) = Action.async { request =>
//    postService.getPost(postId).map {
//      case Some(postWithDetails) => Ok(Json.toJson(postWithDetails))
//      case None => NotFound(Json.obj("error" -> "Post not found"))
//    }
//  }
//
//  def createBid(postId: Long, userId: Long) = Action.async(parse.json) { request =>
//    logger.info(s"Creating bid for postId: $postId, userId: $userId, body: ${request.body.toString()}")
//    request.body.validate[BidInput].fold(
//      errors => {
//        logger.error(s"Invalid bid input: $errors")
//        Future.successful(BadRequest(Json.obj("error" -> s"Invalid bid input: $errors")))
//      },
//      bidInput => {
//        bidService.createBid(postId, userId, bidInput).map {
//          case Right(bidId) => Ok(Json.obj("id" -> bidId))
//          case Left(error) => BadRequest(Json.obj("error" -> error))
//        }
//      }
//    )
//  }
//}

//
//package controllers
//
//import play.api.mvc._
//import services.{BidService, BidInput, PostService, PostWithDetails, ProductDetailInput, RemainingBidTime}
//import play.api.libs.json._
//import javax.inject.Inject
//import scala.concurrent.{ExecutionContext, Future}
//import models.ProductDetail
//import play.api.Logger
//
//class PostController @Inject()(postService: PostService, bidService: BidService, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {
//  private val logger = Logger(this.getClass)
//
//  implicit val productDetailInputFormat: Format[ProductDetailInput] = Json.format[ProductDetailInput]
//  implicit val productDetailFormat: Format[ProductDetail] = Json.format[ProductDetail]
//  implicit val remainingBidTimeFormat: Format[RemainingBidTime] = Json.format[RemainingBidTime]
//  implicit val postWithDetailsFormat: Format[PostWithDetails] = Json.format[PostWithDetails]
//  implicit val bidInputFormat: Format[BidInput] = Json.format[BidInput]
//
//  def create(userId: Long) = Action.async(parse.json) { request =>
//    val postType = (request.body \ "postType").as[String]
//    if (!Seq("blog", "product").contains(postType)) {
//      logger.error(s"Invalid postType: $postType")
//      Future.successful(BadRequest(Json.obj("error" -> "Invalid postType, must be 'blog' or 'product'")))
//    } else {
//      logger.info(s"Request body: ${request.body.toString()}")
//      val title = (request.body \ "title").as[String]
//      val content = (request.body \ "content").as[String]
//      val status = (request.body \ "status").as[String]
//      val productDetails = if (postType == "product") {
//        val result = request.body.as[JsObject].validate[ProductDetailInput]
//        logger.debug(s"Product details validation: $result")
//        result.asOpt
//      } else {
//        None
//      }
//      logger.info(s"Parsed productDetails: $productDetails")
//      if (postType == "product" && productDetails.isEmpty) {
//        logger.error(s"Product details required for product post: ${request.body.toString()}")
//        Future.successful(BadRequest(Json.obj("error" -> "Product details required for product post")))
//      } else {
//        postService.createPost(userId, title, content, status, postType, productDetails).map {
//          case Right(id) => Ok(Json.obj("id" -> id))
//          case Left(error) => BadRequest(Json.obj("error" -> error))
//        }
//      }
//    }
//  }
//
//  def get(postId: Long) = Action.async { request =>
//    logger.info(s"Received GET request for postId: $postId")
//    postService.getPost(postId).map {
//      case Some(postWithDetails) => Ok(Json.toJson(postWithDetails))
//      case None => NotFound(Json.obj("error" -> "Post not found"))
//    }
//  }
//
//  def createBid(postId: Long, userId: Long) = Action.async(parse.json) { request =>
//    logger.info(s"Received POST request for createBid, postId: $postId, userId: $userId")
//    logger.debug(s"Request headers: ${request.headers}")
//    logger.debug(s"Request body: ${request.body.toString()}")
//    request.body.validate[BidInput].fold(
//      errors => {
//        logger.error(s"JSON validation failed for bid input: $errors")
//        Future.successful(BadRequest(Json.obj("error" -> s"Invalid bid input: $errors")))
//      },
//      bidInput => {
//        logger.info(s"Validated bid input: price=${bidInput.price}, quantity=${bidInput.quantity}")
//        logger.debug(s"Calling BidService.createBid for postId: $postId, userId: $userId")
//        bidService.createBid(postId, userId, bidInput).map { result =>
//          logger.debug(s"BidService.createBid result: $result")
//          result match {
//            case Right(bidId) =>
//              logger.info(s"Successfully created bid with ID: $bidId")
//              Ok(Json.obj("id" -> bidId))
//            case Left(error) =>
//              logger.error(s"Bid creation failed: $error")
//              BadRequest(Json.obj("error" -> error))
//          }
//        }.recover {
//          case e: Exception =>
//            logger.error(s"Unexpected error in createBid: ${e.getMessage}", e)
//            InternalServerError(Json.obj("error" -> s"Unexpected error: ${e.getMessage}"))
//        }
//      }
//    )
//  }
//}