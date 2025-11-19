package org.example.project.data.commonData

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
    val id: String = "",
    val title: String,
    val description: String,
    val comments: List<Comment>,
    val group: Group,
    val assignee: User,
    val dueDate: Long,
    val geotag: String,
    val priority: Priority,
    val status: Status
)
