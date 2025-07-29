package controllers

import play.api.mvc._
import services.{PostService, PostWithDetails, ProductDetailInput, RemainingBidTime}
import play.api.libs.json._
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import play.api.Configuration
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import models.ProductDetail

class PostController @Inject()(postService: PostService, config: Configuration, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  private val jwtSecret = config.get[String]("jwt.secret")

  implicit val productDetailInputFormat: Format[ProductDetailInput] = Json.format[ProductDetailInput]
  implicit val productDetailFormat: Format[ProductDetail] = Json.format[ProductDetail]
  implicit val remainingBidTimeFormat: Format[RemainingBidTime] = Json.format[RemainingBidTime]
  implicit val postWithDetailsFormat: Format[PostWithDetails] = Json.format[PostWithDetails]

  private def withUserId(request: Request[JsValue])(action: Long => Future[Result]): Future[Result] = {
    val authHeader = request.headers.get("Authorization")
    println(s"Authorization header: $authHeader")
    authHeader.flatMap { auth =>
      try {
        val token = auth.replace("Bearer ", "")
        println(s"Token: $token")
        val verifier = JWT.require(Algorithm.HMAC256(jwtSecret)).build()
        val jwt = verifier.verify(token)
        val userIdStr = jwt.getClaim("userId").asString()
        println(s"JWT Claims: userId=$userIdStr")
        Option(userIdStr).flatMap { idStr =>
          try {
            Some(idStr.toLong)
          } catch {
            case _: NumberFormatException =>
              println(s"Invalid userId format: $idStr")
              None
          }
        }
      } catch {
        case e: JWTVerificationException =>
          println(s"JWT verification failed: ${e.getMessage}")
          None
      }
    } match {
      case Some(userId) => action(userId)
      case None => Future.successful(Unauthorized(Json.obj("error" -> "Invalid token")).withHeaders("Content-Type" -> "application/json"))
    }
  }

  def create(userId: Long) = Action.async(parse.json) { request =>
    withUserId(request) { authUserId =>
      if (authUserId != userId) {
        Future.successful(Forbidden(Json.obj("error" -> "Unauthorized user")))
      } else {
        val postType = (request.body \ "postType").as[String]
        if (!Seq("blog", "product").contains(postType)) {
          Future.successful(BadRequest(Json.obj("error" -> "Invalid postType, must be 'blog' or 'product'")))
        } else {
          val title = (request.body \ "title").as[String]
          val content = (request.body \ "content").as[String]
          val status = (request.body \ "status").as[String]
          val productDetails = if (postType == "product") {
            request.body.as[JsObject].validate[ProductDetailInput].asOpt
          } else {
            None
          }
          if (postType == "product" && productDetails.isEmpty) {
            Future.successful(BadRequest(Json.obj("error" -> "Product details required for product post")))
          } else {
            postService.createPost(userId, title, content, status, postType, productDetails).map {
              case Right(id) => Ok(Json.obj("id" -> id))
              case Left(error) => BadRequest(Json.obj("error" -> error))
            }
          }
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