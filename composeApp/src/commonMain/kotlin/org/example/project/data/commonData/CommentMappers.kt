package org.example.project.data.commonData

import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import org.example.project.data.network.models.AddCommentRequest
import kotlin.time.Instant
import org.example.project.data.network.models.CommentDto
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Comment.toCommentDto() : CommentDto {
    return CommentDto(
        id = this.id,
        taskId = this.taskId,
        authorId = this.author,
        text =  this.content,
        createdAt = Instant.fromEpochMilliseconds(this.timestamp).toString()
    )
}

@OptIn(ExperimentalTime::class)
fun CommentDto.toComment() : Comment {
    return Comment(
        id = this.id,
        taskId = this.taskId,
        author = this.authorId,
        content =  this.text,
        timestamp = Instant.parse(this.createdAt +
                TimeZone.currentSystemDefault().offsetAt(Clock.System.now())).toEpochMilliseconds()
    )
}


fun Comment.toAddCommentRequest() : AddCommentRequest {
    return AddCommentRequest(
        authorId =  author,
        text = content
    )
}