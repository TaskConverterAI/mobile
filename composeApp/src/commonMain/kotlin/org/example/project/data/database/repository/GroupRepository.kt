package org.example.project.data.database.repository

import co.touchlab.kermit.Logger
import org.example.project.AppDependencies
import org.example.project.data.commonData.Group
import org.example.project.data.commonData.Privileges
import org.example.project.data.commonData.User
import org.example.project.data.network.GroupApiService
import org.example.project.data.network.models.AddMemberRequest
import org.example.project.data.network.models.CreateGroupRequest
import org.example.project.data.network.models.LeaveGroupRequest
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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

                // Получаем noteRepository для подсчета заметок
                val noteRepository = AppDependencies.container.noteRepository

                for (group in response) {
                    // Получаем реальное количество заметок для группы
                    val noteCount = try {
                        noteRepository.getGroupNoteCount(group.id)
                    } catch (e: Exception) {
                        Logger.e { "Error getting note count for group ${group.id}: ${e.message}" }
                        0
                    }

                    groupList.add(
                        Group(
                            id = group.id,
                            name = group.name,
                            description = group.description,
                            ownerId = 0L, // Не приходит в GroupSummaryDto
                            memberCount = group.memberCount,
                            createdAt = Instant.parse(group.createdAt).toEpochMilliseconds(),
                            taskCount = noteCount
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
                val memberList: MutableList<User> = mutableListOf()
                for (member in response.members){
                    memberList.add(
                        User(
                            id = member.userId,
                            email = member.email,
                            username = member.username,
                            privileges = Privileges.valueOf(member.role)
                        )
                    )
                }

                // Получаем реальное количество заметок для группы
                val noteRepository = AppDependencies.container.noteRepository
                val noteCount = try {
                    noteRepository.getGroupNoteCount(groupId)
                } catch (e: Exception) {
                    Logger.e { "Error getting note count for group $groupId: ${e.message}" }
                    0
                }

                val group = Group(
                    id = response.id,
                    name = response.name,
                    description = response.description,
                    ownerId = response.ownerId,
                    memberCount = response.members.size,
                    members = memberList,
                    createdAt = Instant.parse(response.createdAt).toEpochMilliseconds(),
                    taskCount = noteCount
                )

                group
            },
            onFailure = { e ->
                Logger.e {e.message.toString() + "EROOOOR"}
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
                    createdAt = Instant.parse(response.createdAt).toEpochMilliseconds(),
                    taskCount = 0 // У новой группы пока нет заметок
                )
            },
            onFailure = { res ->
                Logger.d { res.message.toString() }
                null
            }
        )

        if (retVal != null) {
            Logger.d { "group created" }
        } else {
            Logger.d { "group not created" }
        }

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

    suspend fun addMemberInGroup(groupId: Long, userNameOrEmail: String, role: Privileges) : User? {
        val addMemberRequest = AddMemberRequest(userNameOrEmail, role.name)
        val result = groupApiService?.addMemberInGroup(groupId, addMemberRequest)

        val retVal = result?.fold(
            onSuccess = { response ->
                User(
                    id = response.userId,
                    email = response.email,
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

    // Перегруженный метод с дефолтной ролью member
    suspend fun addMemberInGroup(groupId: Long, userNameOrEmail: String) : User? {
        return addMemberInGroup(groupId, userNameOrEmail, Privileges.member)
    }

    suspend fun deleteMemberFromGroup(groupId: Long, userId: Long) {
        groupApiService?.removeMemberFromGroup(groupId, userId)

    }

    suspend fun leaveGroup(groupId: Long, userId: Long) {
        groupApiService?.leaveGroup(groupId = groupId, LeaveGroupRequest(userId))
    }
}