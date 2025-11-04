package com.example.taskconvertaiapp.shared.data.auth

interface AuthRepository {
    suspend fun signUp(login: String, email: String, password: String): Boolean
    suspend fun signIn(login: String, password: String): Boolean
}
