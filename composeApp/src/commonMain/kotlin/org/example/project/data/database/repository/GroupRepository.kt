package org.example.project.data.database.repository

import org.example.project.data.commonData.Group
import org.example.project.data.commonData.User
import org.example.project.data.network.GroupApiService
import org.example.project.data.network.models.AddMemberRequest
import org.example.project.data.network.models.CreateGroupRequest
import org.example.project.data.network.models.UpdateGroupRequest

class GroupRepository (private val groupApiService: GroupApiService? = null) {
    /**
     * Получить все группы
     */
    suspend fun getAllGroups(): List<Group> {
        val result = groupApiService?.getAllGroups()

        result?.fold(
            onSuccess = { response ->

            },
            onFailure = { error ->

            }
        )
    }


    suspend fun getGroupById(groupId: String): Group {
        val result = groupApiService?.getGroupById(groupId)

        result?.fold(
            onSuccess = { response ->

            },
            onFailure = { error ->

            }
        )
    }

    suspend fun createGroup(group: Group){

        val userMembersIds:  MutableList<String> = mutableListOf();
        for (user in group.users) {
            userMembersIds.add(user.id.toString())
        }
        val createGroupRequest = CreateGroupRequest(group.name, group.description, userMembersIds)
        val result = groupApiService?.createGroup(createGroupRequest)

        result?.fold(
            onSuccess = { response ->

            },
            onFailure = { error ->

            }
        )
    }

    suspend fun updateGroup(group: Group){
        val updateGroupRequest = UpdateGroupRequest(name = group.name, description = group.description)
        val result = groupApiService?.updateGroup(groupId = group.id.toString(), updateGroupRequest)
        result?.fold(
            onSuccess = { response ->

            },
            onFailure = { error ->

            }
        )
    }

    suspend fun deleteGroup(groupId: String){
        val result = groupApiService?.deleteGroup(groupId = groupId)
        result?.fold(
            onSuccess = { response ->

            },
            onFailure = { error ->

            }
        )
    }

    suspend fun addMemberInGroup(groupId: String, user: User){
        val addMemberRequest = AddMemberRequest(usernameOrEmail = user.email, role = "member")
        val result = groupApiService?.addMemberInGroup(groupId, addMemberRequest)
        result?.fold(
            onSuccess = { response ->

            },
            onFailure = { error ->

            }
        )
    }

    suspend fun deleteMemberFromGroup(groupId: String, userId: String) {
        val result = groupApiService?.removeMemberFromGroup(groupId, userId)
        result?.fold(
            onSuccess = { response ->

            },
            onFailure = { error ->

            }
        )
    }

    suspend fun leaveGroup(groupId: String){
        val result = groupApiService?.leaveGroup(groupId = groupId)
        result?.fold(
            onSuccess = { response ->

            },
            onFailure = { error ->

            }
        )

    }
}