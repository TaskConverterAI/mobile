package org.example.project.data.network.models

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class GroupDto @OptIn(ExperimentalTime::class) constructor(
    val id: Long,
    val name: String,
    val description: String,
    val ownerId: Long,
    val createdAt: String,
    val memberCount: Int
)

@Serializable
data class GroupSummaryDto @OptIn(ExperimentalTime::class) constructor(
    val id: Long,
    val name: String,
    val description: String,
    val memberCount: Int,
    val createdAt: String
)

@Serializable
data class GroupMemberDto @OptIn(ExperimentalTime::class) constructor(
    val userId:	Long,
    val username: String,
    val email: String,
    val role: String,
    val joinedAt: String
)

@Serializable
data class GroupDetailsDto @OptIn(ExperimentalTime::class) constructor(
    val id:	Long,
    val name: String,
    val description: String,
    val ownerId: Long,
    val createdAt:	String,
    val userRole: String,
    val memberCount: Int,
    val members: List<GroupMemberDto>
)

@Serializable
data class UpdateGroupRequest (
    val name: String,
    val description: String
)

@Serializable
data class AddMemberRequest (
    val usernameOrEmail: String,
    val role: String
)

@Serializable
data class CreateGroupRequest (
    val name: String,
    val description: String,
    val userId : Long
)

@Serializable
data class LeaveGroupRequest (
    val transferOwnershipTo: Long
)

@Serializable
data class OwnershipTransferResponse (
    val message: String,
    val eligibleMembers: List<MemberInfo>
)

@Serializable
data class MemberInfo (
    val userId: Long,
    val username: String,
    val email: String
)
