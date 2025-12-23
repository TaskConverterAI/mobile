package org.example.project.data.auth

data class AuthResult (
    val success: Boolean,
    val errors: List<String>,
    val isServerError: Boolean = false // Флаг для серверных ошибок (500, сетевые проблемы и т.д.)
)

interface AuthRepository {

    suspend fun signUp(login: String, email: String, password: String): AuthResult
    suspend fun signIn(login: String, password: String): AuthResult
    suspend fun getUserId(): String

    suspend fun getUserIdByToken(): Long

    suspend fun refresh(): Boolean

    suspend fun logout(userId: Long): Boolean

    suspend fun decode(): Pair<Long, String>?
}
