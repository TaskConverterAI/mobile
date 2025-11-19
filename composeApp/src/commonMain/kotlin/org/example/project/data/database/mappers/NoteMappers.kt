package org.example.project.data.database.mappers

import androidx.compose.ui.graphics.Color
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Group
import org.example.project.data.commonData.Note
import org.example.project.data.database.entities.CommentEntity
import org.example.project.data.database.entities.NoteEntity
import org.example.project.data.database.entities.NoteWithTasks

// Convert Note to NoteEntity (только для вставки/обновления)
fun Note.toEntity(id: Long = 0, groupId: String? = null): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        geotag = geotag,
        groupId = groupId,  // Используем переданный groupId
        colorArgb = color.value.toLong(),
        creationDate = creationDate,
        contentMaxLines = contentMaxLines
    )
}

// Convert NoteWithTasks to Note (полное преобразование с группой и задачами)
fun NoteWithTasks.toNote(comments: List<Comment> = emptyList()): Note {
    return Note(
        id = note.id,
        title = note.title,
        content = note.content,
        geotag = note.geotag,
        group = group?.toGroup() ?: Group(
            id = "",
            name = "Без группы",
            description = "",
            ownerId = "",
            memberCount = 0,
            members = mutableListOf(),
            createdAt = "",
            taskCount = 0
        ),
        comments = comments,
        color = Color(note.colorArgb.toULong()),
        creationDate = note.creationDate,
        contentMaxLines = note.contentMaxLines
    )
}

// Convert Comment to CommentEntity for Note
fun Comment.toNoteCommentEntity(noteId: Long): CommentEntity {
    return CommentEntity(
        id = id,
        noteId = noteId,
        author = author,
        content = content,
        timestamp = timestamp
    )
}

// Convert CommentEntity to Comment
fun CommentEntity.toComment(): Comment {
    return Comment(
        id = id,
        author = author,
        content = content,
        timestamp = timestamp
    )
}

