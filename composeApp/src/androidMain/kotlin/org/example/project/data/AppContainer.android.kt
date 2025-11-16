package org.example.project.data

import org.example.project.data.analyzer.AnalyzerRepository
import org.example.project.data.auth.AuthRepository
import org.example.project.data.auth.NetworkAuthRepository
import org.example.project.data.auth.UserAuthPreferencesRepository

actual fun createAuthRepository(userAuthPreferencesRepository: UserAuthPreferencesRepository): AuthRepository {
    return NetworkAuthRepository(userAuthPreferencesRepository)
}

actual fun createTranscribatorRepository(): AnalyzerRepository{
    return TODO("Provide the return value")
}