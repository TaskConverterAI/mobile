package org.example.project.data.commonData

data class Group(
    val id: String = "",
    val name: String,
    val description: String,
    val users: List<User>
)
