package org.example.project.data.database.repository

import org.example.project.data.commonData.Group
import org.example.project.data.commonData.Privileges
import org.example.project.data.commonData.User
import org.example.project.data.network.GroupApiService
import org.example.project.data.network.models.AddMemberRequest
import org.example.project.data.network.models.CreateGroupRequest
import org.example.project.data.network.models.LeaveGroupRequest
import kotlin.time.ExperimentalTime

class GroupRepository (private val groupApiService: GroupApiService? = null) {
    /**
     * Получить все группы
     */
    @OptIn(ExperimentalTime::class)
    suspend fun getAllGroups(userId: Long): List<Group>? {
        println("GroupRepository: getAllGroups called")
        val result = groupApiService?.getAllGroups(userId = userId)

        println("GroupRepository: API result = $result")
        val retVal = result?.fold(
            onSuccess = { response ->
                println("GroupRepository: Success, received ${response.size} groups")
                val groupList: MutableList<Group> = mutableListOf()

                for (group in response) {
                    groupList.add(
                        Group(
                            id = group.id,
                            name = group.name,
                            description = group.description,
                            ownerId = 0,
                            memberCount = group.memberCount,
                            createdAt = group.createdAt.toEpochMilliseconds(),
                            taskCount = 0
                        )
                    )
                }

                println("GroupRepository: Returning ${groupList.size} groups")
                groupList
            },
            onFailure = { error ->
                println("GroupRepository: Error - ${error.message}")
                error.printStackTrace()
                null
            }
        )

        println("GroupRepository: Final result = $retVal")
        return retVal
    }

    @OptIn(ExperimentalTime::class)
    suspend fun getGroupById(groupId: Long): Group? {
        val result = groupApiService?.getGroupById(groupId)

        val retVal = result?.fold(
            onSuccess = { response ->
                val memberList: MutableList<User> = mutableListOf();
                for (member in response.members){
                    memberList.add(
                        User(
                            id = member.userId,
                            username = member.username,
                            privileges = Privileges.valueOf(member.role)
                        )
                    )
                }

                val group = Group(
                    id = response.id,
                    name = response.name,
                    description = response.description,
                    ownerId = response.ownerId,
                    memberCount = response.members.size,
                    members = memberList,
                    createdAt = response.createdAt.toEpochMilliseconds(),
                    taskCount = 0
                )

                group
            },
            onFailure = { _ ->
                null
            }
        )

        return retVal
    }

    @OptIn(ExperimentalTime::class)
    suspend fun createGroup(group: Group, userId: Long): Group? {
        val createGroupRequest = CreateGroupRequest(group.name, group.description, userId)
        val result = groupApiService?.createGroup(createGroupRequest)

        val retVal = result?.fold(
            onSuccess = { response ->
                Group(
                    id = response.id,
                    name = response.name,
                    description = response.description,
                    ownerId = response.ownerId,
                    memberCount = response.memberCount,
                    createdAt = response.createdAt.toEpochMilliseconds(),
                    taskCount = 0
                )
            },
            onFailure = { _ ->
                null
            }
        )

        return retVal
    }

//    suspend fun updateGroup(group: Group): Group?{
//        val updateGroupRequest = UpdateGroupRequest(name = group.name, description = group.description)
//        val result = groupApiService?.updateGroup(groupId = group.id, updateGroupRequest)
//        val retVal  = result?.fold(
//            onSuccess = { response ->
//
//                val group = Group(
//                    id = response.id,
//                    name = response.name,
//                    description = response.description,
//                    ownerId = response.ownerId,
//                    memberCount = response.memberCount,
//                    createdAt = response.createdAt,
//                    taskCount = 0
//                )
//                group
//            },
//            onFailure = { error ->
//                null
//            }
//        )
//        return retVal
//    }

    suspend fun deleteGroup(groupId: Long, userId: Long) {
        groupApiService?.deleteGroup(groupId = groupId, userId)
    }

    suspend fun addMemberInGroup(groupId: Long, user: User) : User? {
        val addMemberRequest = AddMemberRequest(usernameOrEmail = user.email)
        val result = groupApiService?.addMemberInGroup(groupId, addMemberRequest)

        val retVal = result?.fold(
            onSuccess = { response ->
                User(
                    id = response.userId,
                    username = response.username,
                    privileges = Privileges.valueOf(response.role)
                )
            },
            onFailure = { _ ->
                null
            }
        )

        return retVal
    }

    suspend fun deleteMemberFromGroup(groupId: Long, userId: Long) {
        groupApiService?.removeMemberFromGroup(groupId, userId)

    }

    suspend fun leaveGroup(groupId: Long, userId: Long) {
        groupApiService?.leaveGroup(groupId = groupId, LeaveGroupRequest(userId))
    }
}