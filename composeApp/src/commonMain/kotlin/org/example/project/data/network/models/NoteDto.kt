package org.example.project.data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(
    val id: Long? = null,
    val title: String,
    val content: String,
    val geotag: String,
    val groupId: Long? = null,
    val groupName: String? = null,
    val groupDescription: String? = null,
    val colorArgb: Long,
    val creationDate: Long,
    val contentMaxLines: Int,
    val comments: List<CommentDto> = emptyList(),
    val lastModified: Long? = null,
    val isDeleted: Boolean = false
)

@Serializable
data class CommentDto(
    val id: Long? = null,
    val text: String,
    val author: String,
    val timestamp: Long
)

@Serializable
data class SyncRequest(
    val lastSyncTimestamp: Long,
    val notes: List<NoteDto>
)

@Serializable
data class SyncResponse(
    val notes: List<NoteDto>,
    val deletedNoteIds: List<Long>,
    val syncTimestamp: Long,
    val success: Boolean,
    val message: String? = null
)

