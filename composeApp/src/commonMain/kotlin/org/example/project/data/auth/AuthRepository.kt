package org.example.project.data.auth

import org.example.project.data.commonData.User

interface AuthRepository {

    suspend fun signUp(login: String, email: String, password: String): Boolean
    suspend fun signIn(login: String, password: String): Boolean
    suspend fun getUserId(): String

    suspend fun getUserIdByToken(): Long

    suspend fun refresh(): Boolean

    suspend fun logout(userId: Long): Boolean

    suspend fun decode(): Pair<Long, String>?
}
