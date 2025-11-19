package org.example.project.data

import org.example.project.data.analyzer.AnalyzerRepository
import org.example.project.data.analyzer.DefaultAnalyzerRepository
import org.example.project.data.auth.AuthRepository
import org.example.project.data.auth.NetworkAuthRepository
import org.example.project.data.auth.UserAuthPreferencesRepository
import org.example.project.data.network.NoteApiService
import org.example.project.network.RetrofitClient
import org.example.project.data.groups.GroupRepository

actual fun createAuthRepository(userAuthPreferencesRepository: UserAuthPreferencesRepository): AuthRepository {
    return NetworkAuthRepository(userAuthPreferencesRepository)
}

actual fun createAnalyzerRepository(): AnalyzerRepository{
    return DefaultAnalyzerRepository()
}

actual fun createNoteApiService(): NoteApiService? {
    return RetrofitClient.createNoteApiService()
}

actual fun createGroupRepository(): GroupRepository {
    TODO("Not yet implemented")
}
