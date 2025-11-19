package org.example.project.data.commonData

data class Group(
    val id: String,
    val name: String,
    val description: String,
    val ownerId: String,
    val memberCount: Int,
    val members: MutableList<User> = mutableListOf(),
    val createdAt: String,
    val taskCount: Int = 0
)
