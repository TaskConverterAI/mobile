package org.example.project.data.commonData

data class Group(
    val id: Long,
    val name: String,
    val description: String,
    val ownerId: Long,
    val memberCount: Int,
    val members: MutableList<User> = mutableListOf(),
    val createdAt: Long,
    val taskCount: Int = 0
)
