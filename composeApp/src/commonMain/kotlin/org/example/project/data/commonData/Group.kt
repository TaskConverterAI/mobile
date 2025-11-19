package org.example.project.data.commonData

import org.example.project.data.network.models.GroupMemberDto

data class Group(
    val id: String,
    val name: String,
    val description: String,
    val ownerId: String,
    val memberCount: Int,
    val createdAt: String
)

data class GroupDetails (
    val id:	String,
    val name: String,
    val description: String,
    val ownerId: String,
    val members: List<User>,
    val createdAt:	String
)