package org.example.project.data.database.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.data.commonData.User
import org.example.project.data.database.AppDatabase
import org.example.project.data.database.entities.UserEntity

class UserRepository(
    private val database: AppDatabase
) {
    private val userDao = database.userDao()

    suspend fun getUserById(userId: Long): User? {
        val userEntity = userDao.getById(userId)
        return userEntity?.let {
            User(
                id = it.id,
                email = it.email,
                username = it.username,
                privileges = it.privileges
            )
        }
    }

    suspend fun insertUser(user: User) {
        val userEntity = UserEntity(
            id = user.id,
            email = user.email,
            username = user.username,
            privileges = user.privileges
        )
        userDao.insert(userEntity)
    }

    suspend fun updateUser(user: User) {
        val userEntity = UserEntity(
            id = user.id,
            email = user.email,
            username = user.username,
            privileges = user.privileges
        )
        userDao.update(userEntity)
    }

    suspend fun deleteUser(user: User) {
        val userEntity = UserEntity(
            id = user.id,
            email = user.email,
            username = user.username,
            privileges = user.privileges
        )
        userDao.delete(userEntity)
    }

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { userEntities ->
            userEntities.map { entity ->
                User(
                    id = entity.id,
                    email = entity.email,
                    username = entity.username,
                    privileges = entity.privileges
                )
            }
        }
    }
}

