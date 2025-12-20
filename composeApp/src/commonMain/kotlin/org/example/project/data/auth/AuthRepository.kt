package org.example.project.data.auth

import org.example.project.data.commonData.User

public data class AuthResult (
    val success: Boolean,
    val errors: List<String>
)

interface AuthRepository {

    suspend fun signUp(login: String, email: String, password: String): AuthResult
    suspend fun signIn(login: String, password: String): Boolean
    suspend fun getUserId(): String

    suspend fun getUserIdByToken(): Long

    suspend fun refresh(): Boolean

    suspend fun logout(userId: Long): Boolean

    suspend fun decode(): Pair<Long, String>?
}
