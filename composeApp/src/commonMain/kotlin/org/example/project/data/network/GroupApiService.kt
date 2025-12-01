package org.example.project.data.network

import org.example.project.data.network.models.AddMemberRequest
import org.example.project.data.network.models.CreateGroupRequest
import org.example.project.data.network.models.GroupDetailsDto
import org.example.project.data.network.models.GroupDto
import org.example.project.data.network.models.GroupMemberDto
import org.example.project.data.network.models.GroupSummaryDto
import org.example.project.data.network.models.LeaveGroupRequest
import org.example.project.data.network.models.OwnershipTransferResponse
import org.example.project.data.network.models.UpdateGroupRequest

interface GroupApiService {

    suspend fun getAllGroups(userId: Long): Result<List<GroupSummaryDto>>

    suspend fun getGroupById(groupId: Long): Result<GroupDetailsDto>

    suspend fun createGroup(createGroupRequest: CreateGroupRequest): Result<GroupDto>

//    suspend fun updateGroup(groupId: String, updateGroupRequest: UpdateGroupRequest): Result<GroupDto>

    suspend fun deleteGroup(groupId: Long, userId: Long): Result<Unit>

    suspend fun addMemberInGroup(groupId: Long, addMemberRequest: AddMemberRequest): Result<GroupMemberDto>

    suspend fun removeMemberFromGroup(groupId: Long, userId: Long): Result<Unit>

    suspend fun leaveGroup(groupId: Long, leaveGroupRequest: LeaveGroupRequest?): Result<OwnershipTransferResponse>
}
