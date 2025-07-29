package models
import java.sql.Timestamp
import play.api.libs.json._

case class Comment(id: Long, postId: Long, userId: Long, content: String, createdAt: Timestamp, updatedAt: Timestamp)

object Comment {
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    override def writes(timestamp: Timestamp): JsValue = JsString(timestamp.toString)
    override def reads(json: JsValue): JsResult[Timestamp] = json match {
      case JsString(str) =>
        try {
          JsSuccess(Timestamp.valueOf(str))
        } catch {
          case e: IllegalArgumentException => JsError(s"Invalid timestamp format: $str")
        }
      case _ => JsError("String value expected")
    }
  }

  implicit val format: Format[Comment] = Json.format[Comment]
}