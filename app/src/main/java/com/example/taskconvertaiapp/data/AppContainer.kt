package com.example.taskconvertaiapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.taskconvertaiapp.data.auth.AuthRepository
import com.example.taskconvertaiapp.data.auth.NetworkAuthRepository
import com.example.taskconvertaiapp.data.auth.UserAuthPreferencesRepository

interface AppContainer {
    val authRepository: AuthRepository
}


class DefaultAppContainer(dataStore: DataStore<Preferences>): AppContainer {

    override val authRepository: AuthRepository = NetworkAuthRepository(
        UserAuthPreferencesRepository(dataStore))
}