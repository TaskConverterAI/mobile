package com.example.taskconvertaiapp.shared.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

import com.example.taskconvertaiapp.shared.data.auth.AuthRepository
import com.example.taskconvertaiapp.shared.data.auth.UserAuthPreferencesRepository

interface AppContainer {
    val authRepository: AuthRepository
}

// Expect function to create platform-specific AuthRepository
expect fun createAuthRepository(userAuthPreferencesRepository: UserAuthPreferencesRepository): AuthRepository

class DefaultAppContainer(dataStore: DataStore<Preferences>): AppContainer {
    private val userAuthPreferencesRepository = UserAuthPreferencesRepository(dataStore)

    override val authRepository: AuthRepository = createAuthRepository(userAuthPreferencesRepository)
}
