package org.example.project.data.database.repository

import org.example.project.data.commonData.Group
import org.example.project.data.commonData.Privileges
import org.example.project.data.commonData.User
import org.example.project.data.network.GroupApiService
import org.example.project.data.network.models.AddMemberRequest
import org.example.project.data.network.models.CreateGroupRequest
import org.example.project.data.network.models.UpdateGroupRequest



class GroupRepository (private val groupApiService: GroupApiService? = null) {
    /**
     * Получить все группы
     */
    suspend fun getAllGroups(): List<Group>? {
        val result = groupApiService?.getAllGroups()
        val retVal = result?.fold(
            onSuccess = { response ->
                val groupList: MutableList<Group> = mutableListOf()
                for (group in response) {

                    groupList.add(
                        Group(
                            id = group.id,
                            name = group.name,
                            description = group.description,
                            ownerId = group.ownerId,
                            memberCount = group.memberCount,
                            createdAt = group.createdAt,
                            taskCount = 0
                        )
                    )
                }
                groupList
            },
            onFailure = { error ->
               null
            }
        )
        return retVal
    }


    suspend fun getGroupById(groupId: String): Group? {
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
                    createdAt = response.createdAt,
                    taskCount = 0
                )
                group
            },
            onFailure = { error ->
                null
            }
        )
        return retVal
    }

    suspend fun createGroup(group: Group): Group?{

        val userMembersIds:  MutableList<String> = mutableListOf();
        for (user in group.members) {
            userMembersIds.add(user.id)
        }
        val createGroupRequest = CreateGroupRequest(group.name, group.description, userMembersIds)
        val result = groupApiService?.createGroup(createGroupRequest)

        val retVal = result?.fold(
            onSuccess = { response ->
                Group(
                    id = response.id,
                    name = response.name,
                    description = response.description,
                    ownerId = response.ownerId,
                    memberCount = response.memberCount,
                    createdAt = response.createdAt,
                    taskCount = 0
                )
            },
            onFailure = { error ->
                null
            }
        )
        return retVal
    }

    suspend fun updateGroup(group: Group): Group?{
        val updateGroupRequest = UpdateGroupRequest(name = group.name, description = group.description)
        val result = groupApiService?.updateGroup(groupId = group.id, updateGroupRequest)
        val retVal  = result?.fold(
            onSuccess = { response ->

                val group = Group(
                    id = response.id,
                    name = response.name,
                    description = response.description,
                    ownerId = response.ownerId,
                    memberCount = response.memberCount,
                    createdAt = response.createdAt,
                    taskCount = 0
                )
                group
            },
            onFailure = { error ->
                null
            }
        )
        return retVal
    }

    suspend fun deleteGroup(groupId: String){
        groupApiService?.deleteGroup(groupId = groupId)
    }

    suspend fun addMemberInGroup(groupId: String, user: User) : User?{
        val addMemberRequest = AddMemberRequest(usernameOrEmail = user.email, role = "member")
        val result = groupApiService?.addMemberInGroup(groupId, addMemberRequest)
        val retVal = result?.fold(
            onSuccess = { response ->
                User(
                    id = response.userId,
                    username = response.username,
                    privileges = Privileges.valueOf(response.role)
                )
            },
            onFailure = { error ->
                null
            }
        )
        return retVal
    }

    suspend fun deleteMemberFromGroup(groupId: String, userId: String) {
        groupApiService?.removeMemberFromGroup(groupId, userId)

    }

    suspend fun leaveGroup(groupId: String){
        groupApiService?.leaveGroup(groupId = groupId)
    }
}