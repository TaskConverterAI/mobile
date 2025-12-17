package org.example.project.data.commonData

data class Comment(
    val id: Long = 0,
    val taskId: Long,
    val author: Long,
    val content: String,
    val timestamp: Long
)
