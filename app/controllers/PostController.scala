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