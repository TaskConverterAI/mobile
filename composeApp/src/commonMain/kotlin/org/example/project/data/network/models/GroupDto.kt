package org.example.project.data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class GroupDto (
    val id: String,
    val name: String,
    val description: String,
    val ownerId: String,
    val memberCount: Int,
    val createdAt: String
)

@Serializable
data class GroupMemberDto (
    val userId:	String,
    val username: String,
    val role: String,
    val joinedAt: String
)

@Serializable
data class GroupDetailsDto (
    val id:	String,
    val name: String,
    val description: String,
    val ownerId: String,
    val members: List<GroupMemberDto>,
    val createdAt:	String
)

@Serializable
data class UpdateGroupRequest (
    val name: String,
    val description: String
)

@Serializable
data class AddMemberRequest (
    val usernameOrEmail: String,
    val role: String = "member"
)

@Serializable
data class CreateGroupRequest (
    val name: String,
    val description: String,
    val initialMemberIds : List<String>
)

