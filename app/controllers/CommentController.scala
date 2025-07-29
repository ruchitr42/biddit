//package controllers
//
//import play.api.mvc._
//import services.CommentService
//import play.api.libs.json.Json
//import javax.inject.Inject
//import scala.concurrent.ExecutionContext
//
//class CommentController @Inject()(commentService: CommentService, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {
//
//  def addComment(postId: Long) = Action.async(parse.json) { request =>
//    val userId = (request.body \ "userId").as[Long]
//    val content = (request.body \ "content").as[String]
//    commentService.addComment(postId, userId, content).map { id =>
//      Ok(Json.obj("id" -> id))
//    }
//  }
//
//  def getComments(postId: Long) = Action.async {
//    commentService.getComments(postId).map { comments =>
//      Ok(Json.toJson(comments))
//    }
//  }
//}


package controllers

import play.api.mvc._
import services.CommentService
import play.api.libs.json.Json
import models.Comment
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CommentController @Inject()(commentService: CommentService, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def addComment(postId: Long) = Action.async(parse.json) { request =>
    val userId = (request.body \ "userId").as[Long]
    val content = (request.body \ "content").as[String]
    commentService.addComment(postId, userId, content).map { id =>
      Ok(Json.obj("id" -> id))
    }
  }

  def getComments(postId: Long) = Action.async {
    commentService.getComments(postId).map { comments =>
      Ok(Json.toJson(comments))
    }
  }
}