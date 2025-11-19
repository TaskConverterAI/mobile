package org.example.project.data

import org.example.project.data.analyzer.AnalyzerRepository
import org.example.project.data.auth.AuthRepository
import org.example.project.data.auth.UserAuthPreferencesRepository
import org.example.project.data.network.NoteApiService
import org.example.project.data.network.IosNoteApiService

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

        override suspend fun getUserId(): String {
            TODO("Not yet implemented")
        }

    }
}

actual fun createAnalyzerRepository(): AnalyzerRepository{
    return TODO("Provide the return value")
}

actual fun createNoteApiService(): NoteApiService? {
    // TODO: Реализовать полноценный iOS сервис когда будет готова iOS версия
    // Пока возвращаем null, чтобы приложение работало в offline режиме
    return null
    // return IosNoteApiService() // раскомментируйте когда будет реализация
}
