package org.example.project.data.commonData

data class Task(
    val title: String,
    val description: String,
    val comments: List<Comment>,
    val group: String,
    val assignee: String,
    val dueDate: Long,
    val geotag: String)
