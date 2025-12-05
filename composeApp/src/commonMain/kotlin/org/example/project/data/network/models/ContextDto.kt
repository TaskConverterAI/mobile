package org.example.project.data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val remindByLocation: Boolean
)

@Serializable
data class DeadlineDto(
    val time: String,
    val remindByTime: Boolean
)

@Serializable
class CommentDto (
    val id: Long,
    val taskId:	Long,
    val authorId: Long,
    val text: String,
    val createdAt: String
)

@Serializable
class AddCommentRequest(
    val authorId: Long,
    val text: String
)