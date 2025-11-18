package org.example.project.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual


@Serializable
data class JobResponse (
    val jobId: String
)

@Serializable
data class AnalysisJob @OptIn(ExperimentalTime::class) constructor(
    val jobId: String,
    val status: Status,
    val submitterUserId: String,
    val type: JobType,
    @Contextual val createdAt: Instant,
    @Contextual val updatedAt: Instant,
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
data class MeetingSummary (
    val summary: String,
    val tasks: List<TaskItem>
)

@Serializable
data class TaskItem (
    val title: String,
    val description: String,
    val assignee: String?
)

@Serializable
data class Phrase(
    val speaker: String,
    val text: String
)