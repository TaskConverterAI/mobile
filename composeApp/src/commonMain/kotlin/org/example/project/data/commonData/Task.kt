package org.example.project.data.commonData

import kotlinx.serialization.Serializable

enum class Priority {
    HIGH,
    MEDIUM,
    LOW
}

enum class Status {
    TODO,
    IN_PROGRESS,
    DONE
}

data class Task(
    val title: String,
    val description: String,
    val comments: List<Comment>,
    val group: String,
    val assignee: String,
    val dueDate: Long,
    val geotag: String,
    val priority: Priority,
    val status: Status
)
