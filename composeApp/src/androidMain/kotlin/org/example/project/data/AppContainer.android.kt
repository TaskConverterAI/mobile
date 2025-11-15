package org.example.project.data

import org.example.project.data.auth.AuthRepository
import org.example.project.data.auth.NetworkAuthRepository
import org.example.project.data.auth.UserAuthPreferencesRepository
import org.example.project.data.groups.GroupRepository

actual fun createAuthRepository(userAuthPreferencesRepository: UserAuthPreferencesRepository): AuthRepository {
    return NetworkAuthRepository(userAuthPreferencesRepository)
}

actual fun createGroupRepository(): GroupRepository {
    TODO("Not yet implemented")
}
