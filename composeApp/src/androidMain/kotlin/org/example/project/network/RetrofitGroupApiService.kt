package org.example.project.network

import org.example.project.data.network.models.AddMemberRequest
import org.example.project.data.network.models.CreateGroupRequest
import org.example.project.data.network.models.GroupDetailsDto
import org.example.project.data.network.models.GroupDto
import org.example.project.data.network.models.GroupMemberDto
import org.example.project.data.network.models.UpdateGroupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitGroupApiService {
    @GET("auth/groups")
    suspend fun getAllGroups(): Response<List<GroupDto>>

    @GET("auth/groups/{groupId}")
    suspend fun getGroupById(@Path("groupId") groupId: String): Response<GroupDetailsDto>

    @POST("auth/groups")
    suspend fun createGroup(@Body createGroupRequest: CreateGroupRequest): Response<GroupDto>

    @PUT("auth/groups/{groupId}")
    suspend fun updateGroup(
        @Path("groupId") groupId: String,
        @Body group: UpdateGroupRequest
    ): Response<GroupDto>

    @DELETE("auth/groups/{groupId}")
    suspend fun deleteGroup(@Path("groupId") groupId: String): Response<Unit>

    @POST("auth/groups/{groupId}/members")
    suspend fun addMemberInGroup(
        @Path("groupId") groupId: String,
        @Body memberRequest: AddMemberRequest
    ): Response<GroupMemberDto>

    @DELETE("auth/groups/{groupId}/members/{userId}")
    suspend fun removeMemberFromGroup(
        @Path("groupId") groupId: String,
        @Path("userId") userId: String
    ): Response<Unit>

    @POST("auth/groups/{groupId}/leave")
    suspend fun leaveGroup(@Path("groupId") groupId: String): Response<Unit>
}