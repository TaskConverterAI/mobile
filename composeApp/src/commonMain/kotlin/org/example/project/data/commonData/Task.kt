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
    val id: Long,
    val title: String,
    val description: String,
    val comments: List<Comment>,
    val authorId: Long,
    val groupId: Long?,
    val assignee: Long,
    val dueDate: Deadline?,
    val geotag: Location?,
    val priority: Priority,
    val status: Status,
    val createAt: Long = 0
)
