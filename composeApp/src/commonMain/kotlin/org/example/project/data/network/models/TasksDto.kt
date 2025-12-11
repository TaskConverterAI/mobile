package org.example.project.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.project.data.commonData.Status


@Serializable
data class TaskDto(

    val id: Long,
    val title: String,
    val description: String,
    val taskType: String? = null,
    val authorId: Long,
    val priority: String,
    val groupId: Long? = null,
    val doerId: Long,
    val location: LocationDto?,
    val deadline: DeadlineDto?,
    val createdAt: String,
    val status: String
)


@Serializable
data class TaskDetailsDto(

    val id: Long,
    val title: String,
    val description: String,
    val taskType: String? = null,
    val authorId: Long,
    val priority: String,
    val groupId: Long? = null,
    val doerId: Long,
    val location: LocationDto?,
    val deadline: DeadlineDto?,
    val createdAt: String,
    val comments: List<CommentDto>,
    val status: String
)

@Serializable
data class UpdateTaskRequest(

    val id: Long,
    val title: String,
    val description: String,
    val priority: String,
    val doerId: Long,
    val location: LocationDto?,
    val deadline: DeadlineDto?,
    val status: String
)

@Serializable
data class CreateTaskRequest(

    val title: String,
    val description: String,
    val priority: String,
    val groupId: Long? = null,
    val authorId: Long,
    val doerId: Long,
    val location: LocationDto?,
    val deadline: DeadlineDto?,
)
