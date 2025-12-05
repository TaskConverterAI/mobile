package org.example.project.data.groups

import org.example.project.data.commonData.Group

interface GroupRepository {
    suspend fun getGroupInfo(groupId: Int): Group
    suspend fun getAllGroups(): List<Group>
    suspend fun addMember(groupId: Int, memberEmail: String): Boolean
    suspend fun removeMember(groupId: Int, memberEmail: String): Boolean
    suspend fun leaveFrom(groupID: Int, accessorEmail: String): Boolean

}
