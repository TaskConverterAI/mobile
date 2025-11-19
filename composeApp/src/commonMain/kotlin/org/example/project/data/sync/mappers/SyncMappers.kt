package org.example.project.data.sync.mappers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Group
import org.example.project.data.commonData.Note
import org.example.project.data.network.models.CommentDto
import org.example.project.data.network.models.NoteDto

/**
 * Конвертация Note в NoteDto для отправки на сервер
 */
@OptIn(ExperimentalTime::class)
fun Note.toDto(): NoteDto {
    val groupIdStr: String = this.group.id
    return NoteDto(
        id = if (this.id == 0L) null else this.id,
        title = this.title,
        content = this.content,
        geotag = this.geotag,
        groupId = groupIdStr.ifEmpty { null },
        groupName = this.group.name,
        groupDescription = this.group.description,
        colorArgb = this.color.toArgb().toLong(),
        creationDate = this.creationDate,
        contentMaxLines = this.contentMaxLines,
        comments = this.comments.map { it.toDto() },
        lastModified = Clock.System.now().toEpochMilliseconds(),
        isDeleted = false
    )
}

/**
 * Конвертация NoteDto в Note для сохранения локально
 */
fun NoteDto.toNote(): Note {
    val groupIdOrEmpty: String = this.groupId ?: ""
    return Note(
        id = this.id ?: 0L,
        title = this.title,
        content = this.content,
        geotag = this.geotag,
        group = Group(
            id = groupIdOrEmpty,
            name = this.groupName ?: "Без группы",
            description = this.groupDescription ?: "",
            users = emptyList()
        ),
        comments = this.comments.map { it.toComment() },
        color = Color(this.colorArgb.toULong()),
        creationDate = this.creationDate,
        contentMaxLines = this.contentMaxLines
    )
}

/**
 * Конвертация Comment в CommentDto
 */
fun Comment.toDto(): CommentDto {
    return CommentDto(
        id = if (this.id == 0L) null else this.id,
        text = this.content,
        author = this.author,
        timestamp = this.timestamp
    )
}

/**
 * Конвертация CommentDto в Comment
 */
fun CommentDto.toComment(): Comment {
    return Comment(
        id = this.id ?: 0L,
        author = this.author,
        content = this.text,
        timestamp = this.timestamp
    )
}

