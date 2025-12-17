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
import org.example.project.network.RetrofitGroupApiService


class NetworkGroupApiService(
    private val retrofitService: RetrofitGroupApiService
) : GroupApiService {

    override suspend fun getAllGroups(userId: Long): Result<List<GroupSummaryDto>> {
        return try {
            val response = retrofitService.getAllGroups(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch groups: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGroupById(groupId: Long): Result<GroupDetailsDto> {
        return try {
            val response = retrofitService.getGroupById(groupId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch group: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createGroup(createGroupRequest: CreateGroupRequest): Result<GroupDto> {
        return try {
            val response = retrofitService.createGroup(createGroupRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create group: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    override suspend fun updateGroup(
//        groupId: String,
//        updateGroupRequest: UpdateGroupRequest
//    ): Result<GroupDto> {
//        return try {
//            val response = retrofitService.updateGroup(groupId, updateGroupRequest)
//            if (response.isSuccessful && response.body() != null) {
//                Result.success(response.body()!!)
//            } else {
//                Result.failure(Exception("Failed to update group: ${response.message()}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    override suspend fun deleteGroup(groupId: Long, userId: Long): Result<Unit> {
        return try {
            val response = retrofitService.deleteGroup(groupId, userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete group: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addMemberInGroup(
        groupId: Long,
        addMemberRequest: AddMemberRequest
    ): Result<GroupMemberDto> {
        return try {
            val response = retrofitService.addMemberInGroup(groupId, addMemberRequest)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to add member: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeMemberFromGroup(groupId: Long, userId: Long): Result<Unit> {
        return try {
            val response = retrofitService.removeMemberFromGroup(groupId, userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to remove member: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun leaveGroup(groupId: Long, leaveGroupRequest: LeaveGroupRequest?): Result<OwnershipTransferResponse> {
        return try {
            val response = retrofitService.leaveGroup(groupId, leaveGroupRequest)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to leave group: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}