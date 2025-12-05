package org.example.project.data.commonData

import org.example.project.data.network.models.CreateNoteRequest
import org.example.project.data.network.models.NoteDetailsDto
import org.example.project.data.network.models.NoteDto
import org.example.project.data.network.models.UpdateNoteRequest
import org.example.project.ui.screens.notesScreen.NotesScreen
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun Note.toNoteDto() : NoteDto {
    return NoteDto(
        id = id,
        title = title,
        description = content,
        location = geotag?.toLocationDto(),
        createdAt = Instant.fromEpochMilliseconds(creationDate).toString(),
        groupId = groupId,
        authorId = authorId
    )
}

@OptIn(ExperimentalTime::class)
fun NoteDto.toNote() : Note {
    return Note(
        id = id,
        title = title,
        content = description,
        geotag = location?.toLocation(),
        creationDate = Instant.parse(createdAt).toEpochMilliseconds(),
        groupId = groupId,
        authorId = authorId,
        comments = emptyList()
    )
}

@OptIn(ExperimentalTime::class)
fun Note.toNoteDetailsDto() : NoteDetailsDto {
    return NoteDetailsDto(
        id = id,
        title = title,
        description = content,
        location = geotag?.toLocationDto(),
        createdAt = Instant.fromEpochMilliseconds(creationDate).toString(),
        groupId = groupId,
        authorId = authorId,
        comments = comments.map { comment -> comment.toCommentDto() }
    )
}

@OptIn(ExperimentalTime::class)
fun NoteDetailsDto.toNote() : Note {
    return Note(
        id = id,
        title = title,
        content = description,
        geotag = location?.toLocation(),
        creationDate = Instant.parse(createdAt).toEpochMilliseconds(),
        groupId = groupId,
        authorId = authorId,
        comments = comments.map { comment -> comment.toComment() }
    )
}

fun Note.toCreateNoteRequest() : CreateNoteRequest {
    return CreateNoteRequest(
        title = title,
        description = content,
        groupId = groupId,
        location = geotag?.toLocationDto(),
        authorId = authorId
    )
}

fun Note.toUpdateNoteRequest() : UpdateNoteRequest {
    return UpdateNoteRequest(
        title = title,
        description = content,

        location = geotag?.toLocationDto(),

    )
}