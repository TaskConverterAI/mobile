package org.example.project.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.example.project.data.database.entities.UserEntity
import org.example.project.data.database.entities.UserWithGroups

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    // Получение пользователя со всеми его группами
    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithGroups(userId: String): UserWithGroups?

    @Transaction
    @Query("SELECT * FROM users")
    fun getAllUsersWithGroups(): Flow<List<UserWithGroups>>
}

