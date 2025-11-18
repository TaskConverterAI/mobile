package org.example.project.data

import org.example.project.data.auth.AuthRepository
import org.example.project.data.auth.NetworkAuthRepository
import org.example.project.data.auth.UserAuthPreferencesRepository
import org.example.project.data.network.NoteApiService
import org.example.project.network.RetrofitClient

actual fun createAuthRepository(userAuthPreferencesRepository: UserAuthPreferencesRepository): AuthRepository {
    return NetworkAuthRepository(userAuthPreferencesRepository)
}

actual fun createNoteApiService(): NoteApiService? {
    return RetrofitClient.createNoteApiService()
}
