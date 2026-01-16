package controllers

import play.api.mvc._
import services.BookmarkService
import play.api.libs.json.Json
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BookmarkController @Inject()(bookmarkService: BookmarkService, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def bookmark(postId: Long) = Action.async(parse.json) { request =>
    val userId = (request.body \ "userId").as[Long]
    bookmarkService.bookmarkPost(postId, userId).map { _ =>
      Ok(Json.obj("status" -> "bookmarked"))
    }
  }

  def unbookmark(postId: Long) = Action.async(parse.json) { request =>
    val userId = (request.body \ "userId").as[Long]
    bookmarkService.unbookmarkPost(postId, userId).map { _ =>
      Ok(Json.obj("status" -> "unbookmarked"))
    }
  }

  def getBookmarks(userId: Long) = Action.async(parse.json) { request =>
    val requestingUserId = (request.body \ "userId").as[Long]
    if (requestingUserId == userId) {
      bookmarkService.getBookmarks(userId).map { postIds =>
        Ok(Json.toJson(postIds))
      }
    } else {
      Future.successful(Forbidden("Unauthorized"))
    }
  }
}