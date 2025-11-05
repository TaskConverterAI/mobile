package org.example.project.data

import org.example.project.data.auth.AuthRepository
import org.example.project.data.auth.UserAuthPreferencesRepository

// Temporary iOS implementation - you'll need to implement a proper iOS networking solution
actual fun createAuthRepository(userAuthPreferencesRepository: UserAuthPreferencesRepository): AuthRepository {
    return object : AuthRepository {
        override suspend fun signUp(login: String, email: String, password: String): Boolean {
            // TODO: Implement iOS-specific network call
            throw NotImplementedError("iOS AuthRepository not yet implemented")
        }

        override suspend fun signIn(login: String, password: String): Boolean {
            // TODO: Implement iOS-specific network call
            throw NotImplementedError("iOS AuthRepository not yet implemented")
        }
    }
}
