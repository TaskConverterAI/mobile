package org.example.project.model

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual


@Serializable
data class JobResponse(
    val jobId: String
)

@Serializable
data class AnalysisJob @OptIn(ExperimentalTime::class) constructor(
    val jobId: String,
    val status: Status,
    val submitterUserId: String,
    val type: JobType,
    val createdAt: String,
    val updatedAt: String,
    val errorMessage: String? = null
)

@Serializable
enum class Status {
    PENDING,
    RUNNING,
    SUCCEEDED,
    FAILED
}

@Serializable
enum class JobType {
    AUDIO,
    TASK
}

@Serializable
data class MeetingSummary(
    val summary: String,
    val tasks: List<TaskItem>
)

@Serializable
data class TaskItem(
    val title: String,
    val description: String,
    val assignee: String?
)

@Serializable
data class PublicSpeakerUtterance(
    val speaker: String,
    val text: String
)

@Serializable
data class TaskRequest(
    val description: String,
    val geo: GeoLocation?,
    val name: String?,
    val group: String?,
    val data: String?,
    val date: String?
)

@Serializable
data class GeoLocation(
    val latitude: Double,
    val longitude: Double
)