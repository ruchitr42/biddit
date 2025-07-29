package models

import java.sql.Timestamp

case class Bookmark(postId: Long, userId: Long, createdAt: Timestamp)