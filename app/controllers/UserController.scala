package controllers

import play.api.mvc._
import services.UserService
import play.api.libs.json.Json
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class UserController @Inject()(userService: UserService, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def register = Action.async(parse.json) { request =>
    val username = (request.body \ "username").as[String]
    val email = (request.body \ "email").as[String]
    val password = (request.body \ "password").as[String]
    val passwordHash = password // Hash in production
    userService.createUser(username, email, passwordHash).map { id =>
      Ok(Json.obj("id" -> id))
    }
  }

  def login = Action.async(parse.json) { request =>
    val email = (request.body \ "email").as[String]
    val password = (request.body \ "password").as[String]
    userService.login(email, password).map {
      case Some(token) => Ok(Json.obj("token" -> token))
      case None => Unauthorized("Invalid credentials")
    }
  }
}