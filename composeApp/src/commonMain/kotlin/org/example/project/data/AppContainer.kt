package org.example.project.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

import org.example.project.data.auth.AuthRepository
import org.example.project.data.auth.UserAuthPreferencesRepository
import org.example.project.data.commonData.Group
import org.example.project.data.groups.GroupRepository

interface AppContainer {
    val authRepository: AuthRepository
    val groupRepository: GroupRepository
}

// Expect function to create platform-specific AuthRepository
expect fun createAuthRepository(userAuthPreferencesRepository: UserAuthPreferencesRepository): AuthRepository

expect fun createGroupRepository(): GroupRepository

class DefaultAppContainer(dataStore: DataStore<Preferences>): AppContainer {
    private val userAuthPreferencesRepository = UserAuthPreferencesRepository(dataStore)

    override val authRepository: AuthRepository = createAuthRepository(userAuthPreferencesRepository)

    override val groupRepository: GroupRepository = createGroupRepository()
}
