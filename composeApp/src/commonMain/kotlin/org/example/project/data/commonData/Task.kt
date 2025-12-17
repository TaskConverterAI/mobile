package org.example.project.data.commonData

import androidx.compose.ui.graphics.Color

enum class Priority(val value: String) {
    HIGH("HIGH"),
    MIDDLE("MIDDLE"),
    LOW("LOW")
}

enum class Status(val value: String) {
    UNDONE("UNDONE"),
    DONE("DONE")
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
    val color: Color = Color.Blue,
    val createAt: Long = 0
)
