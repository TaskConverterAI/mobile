package org.example.project.data.network

import org.example.project.data.network.models.AddMemberRequest
import org.example.project.data.network.models.CreateGroupRequest
import org.example.project.data.network.models.GroupDetailsDto
import org.example.project.data.network.models.GroupDto
import org.example.project.data.network.models.GroupMemberDto
import org.example.project.data.network.models.UpdateGroupRequest

interface GroupApiService {

    suspend fun getAllGroups(): Result<List<GroupDto>>

    suspend fun getGroupById(groupId: String): Result<GroupDetailsDto>

    suspend fun createGroup(createGroupRequest: CreateGroupRequest): Result<GroupDto>

    suspend fun updateGroup(groupId: String, updateGroupRequest: UpdateGroupRequest): Result<GroupDto>

    suspend fun deleteGroup(groupId: String): Result<Unit>

    suspend fun addMemberInGroup(groupId: String, addMemberRequest: AddMemberRequest): Result<GroupMemberDto>

    suspend fun removeMemberFromGroup(groupId: String, userId: String): Result<Unit>

    suspend fun leaveGroup(groupId: String): Result<Unit>
}
