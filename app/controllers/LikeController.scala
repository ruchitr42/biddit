package controllers

import play.api.mvc._
import services.LikeService
import play.api.libs.json.Json
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class LikeController @Inject()(likeService: LikeService, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def like(postId: Long) = Action.async(parse.json) { request =>
    val userId = (request.body \ "userId").as[Long] // From JWT in production
    likeService.likePost(postId, userId).map { _ =>
      Ok(Json.obj("status" -> "liked"))
    }
  }

  def unlike(postId: Long) = Action.async(parse.json) { request =>
    val userId = (request.body \ "userId").as[Long]
    likeService.unlikePost(postId, userId).map { _ =>
      Ok(Json.obj("status" -> "unliked"))
    }
  }

  def getLikeCount(postId: Long) = Action.async {
    likeService.getLikeCount(postId).map { count =>
      Ok(Json.obj("count" -> count))
    }
  }

  def getLikers(postId: Long) = Action.async(parse.json) { request =>
    val userId = (request.body \ "userId").as[Long]
    likeService.getLikers(postId, userId).map { likers =>
      Ok(Json.toJson(likers))
    }
  }
}