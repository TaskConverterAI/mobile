package org.example.project.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.example.project.data.database.entities.GroupEntity
import org.example.project.data.database.entities.GroupUserCrossRef
import org.example.project.data.database.entities.GroupWithNotes
import org.example.project.data.database.entities.GroupWithUsers

@Dao
interface GroupDao {

    // CRUD операции для групп
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: GroupEntity): Long

    @Update
    suspend fun update(group: GroupEntity)

    @Delete
    suspend fun delete(group: GroupEntity)

    @Query("SELECT * FROM `groups` WHERE id = :id")
    suspend fun getById(id: Long): GroupEntity?

    @Query("SELECT * FROM `groups`")
    fun getAllGroups(): Flow<List<GroupEntity>>

    // Операции для связей группа-пользователь
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroupUserCrossRef(crossRef: GroupUserCrossRef)

    @Delete
    suspend fun deleteGroupUserCrossRef(crossRef: GroupUserCrossRef)

    @Query("DELETE FROM group_user_cross_ref WHERE groupId = :groupId")
    suspend fun deleteAllUsersFromGroup(groupId: Long)

    @Query("DELETE FROM group_user_cross_ref WHERE userId = :userId")
    suspend fun deleteUserFromAllGroups(userId: Long)

    // Получение группы со всеми пользователями
    @Transaction
    @Query("SELECT * FROM `groups` WHERE id = :groupId")
    suspend fun getGroupWithUsers(groupId: Long): GroupWithUsers?

    @Transaction
    @Query("SELECT * FROM `groups`")
    fun getAllGroupsWithUsers(): Flow<List<GroupWithUsers>>

    // Получение группы со всеми заметками
    @Transaction
    @Query("SELECT * FROM `groups` WHERE id = :groupId")
    suspend fun getGroupWithNotes(groupId: Long): GroupWithNotes?

    @Transaction
    @Query("SELECT * FROM `groups`")
    fun getAllGroupsWithNotes(): Flow<List<GroupWithNotes>>
}

