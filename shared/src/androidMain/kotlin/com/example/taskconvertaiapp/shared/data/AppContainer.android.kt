package com.example.taskconvertaiapp.shared.data

import com.example.taskconvertaiapp.shared.data.auth.AuthRepository
import com.example.taskconvertaiapp.shared.data.auth.NetworkAuthRepository
import com.example.taskconvertaiapp.shared.data.auth.UserAuthPreferencesRepository

actual fun createAuthRepository(userAuthPreferencesRepository: UserAuthPreferencesRepository): AuthRepository {
    return NetworkAuthRepository(userAuthPreferencesRepository)
}
