package org.example.project.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.example.project.data.analyzer.AnalyzerRepository

import org.example.project.data.auth.AuthRepository
import org.example.project.data.auth.UserAuthPreferencesRepository

interface AppContainer {
    val authRepository: AuthRepository
    val transcribatorRepository: AnalyzerRepository
}

// Expect function to create platform-specific AuthRepository
expect fun createAuthRepository(userAuthPreferencesRepository: UserAuthPreferencesRepository): AuthRepository

expect fun createTranscribatorRepository(): AnalyzerRepository

class DefaultAppContainer(dataStore: DataStore<Preferences>): AppContainer {
    private val userAuthPreferencesRepository = UserAuthPreferencesRepository(dataStore)

    override val authRepository: AuthRepository = createAuthRepository(userAuthPreferencesRepository)
}
