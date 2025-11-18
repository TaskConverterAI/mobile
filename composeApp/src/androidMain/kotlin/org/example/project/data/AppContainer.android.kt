package org.example.project.data

import org.example.project.data.analyzer.AnalyzerRepository
import org.example.project.data.analyzer.DefaultAnalyzerRepository
import org.example.project.data.auth.AuthRepository
import org.example.project.data.auth.NetworkAuthRepository
import org.example.project.data.auth.UserAuthPreferencesRepository

actual fun createAuthRepository(userAuthPreferencesRepository: UserAuthPreferencesRepository): AuthRepository {
    return NetworkAuthRepository(userAuthPreferencesRepository)
}

actual fun createAnalyzerRepository(): AnalyzerRepository{
    return DefaultAnalyzerRepository()
}