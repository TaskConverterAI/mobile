package org.example.project.network

import org.example.project.data.network.models.AddMemberRequest
import org.example.project.data.network.models.CreateGroupRequest
import org.example.project.data.network.models.GroupDetailsDto
import org.example.project.data.network.models.GroupDto
import org.example.project.data.network.models.GroupMemberDto
import org.example.project.data.network.models.GroupSummaryDto
import org.example.project.data.network.models.LeaveGroupRequest
import org.example.project.data.network.models.OwnershipTransferResponse
import org.example.project.data.network.models.UpdateGroupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitGroupApiService {
    @GET("/groups")
    suspend fun getAllGroups(@Query("userId") userId: Long): Response<List<GroupSummaryDto>>

    @GET("/groups/{groupId}")
    suspend fun getGroupById(@Path("groupId") groupId: Long): Response<GroupDetailsDto>

    @POST("/groups")
    suspend fun createGroup(@Body createGroupRequest: CreateGroupRequest): Response<GroupDto>

//    @PUT("auth/groups/{groupId}")
//    suspend fun updateGroup(
//        @Path("groupId") groupId: String,
//        @Body group: UpdateGroupRequest
//    ): Response<GroupDto>

    @DELETE("/groups/{groupId}")
    suspend fun deleteGroup(@Path("groupId") groupId: Long,
                            @Query("userId") userId: Long)
    : Response<Unit>

    @POST("/groups/{groupId}/members")
    suspend fun addMemberInGroup(
        @Path("groupId") groupId: Long,
        @Body memberRequest: AddMemberRequest
    ): Response<GroupMemberDto>

    @DELETE("/groups/{groupId}/members/{userId}")
    suspend fun removeMemberFromGroup(
        @Path("groupId") groupId: Long,
        @Path("userId") userId: Long
    ): Response<Unit>

    @POST("/groups/{groupId}/leave")
    suspend fun leaveGroup(@Path("groupId") groupId: Long,
                           @Body leaveGroupRequest: LeaveGroupRequest?
    ): Response<OwnershipTransferResponse>
}