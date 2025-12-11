package org.example.project.data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(

    val id: Long,
    val title: String,
    val description: String,
    val taskType: String? = null,
    val authorId: Long,
    val location: LocationDto?,
    val groupId: Long? = null,
    val createdAt: String
)

@Serializable
data class NoteDetailsDto(

    val id: Long,
    val title: String,
    val description: String,
    val taskType: String? = null,
    val authorId: Long,
    val location: LocationDto?,
    val groupId: Long? = null,
    val createdAt: String,
    val comments: List<CommentDto>
)

@Serializable
data class CreateNoteRequest(

    val title: String,
    val description: String,
    val groupId: Long? = null,
    val location: LocationDto?,
    val authorId: Long
)

@Serializable
data class UpdateNoteRequest(

    val title: String,
    val description: String,
    val location: LocationDto?,
)
