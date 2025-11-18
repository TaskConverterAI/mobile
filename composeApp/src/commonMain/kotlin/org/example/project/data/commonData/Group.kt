package org.example.project.data.commonData

data class Group(
    val id: Long = 0,
    val name: String,
    val description: String,
    val users: List<User>
)
