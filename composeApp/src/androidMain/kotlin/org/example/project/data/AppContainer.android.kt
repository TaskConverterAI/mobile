package org.example.project.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.example.project.data.analyzer.AnalyzerRepository
import org.example.project.data.analyzer.DefaultAnalyzerRepository
import org.example.project.data.auth.AuthRepository
import org.example.project.data.auth.NetworkAuthRepository
import org.example.project.data.auth.UserAuthPreferencesRepository
import org.example.project.data.network.NoteApiService
import org.example.project.network.RetrofitClient
import org.example.project.data.network.GroupApiService
import org.example.project.data.notifications.NotificationService
import org.example.project.android.notifications.AndroidNotificationService
import org.example.project.android.notifications.NotificationHelper

actual fun createAuthPreferencesRepository(dataStore: DataStore<Preferences>): UserAuthPreferencesRepository {
    return UserAuthPreferencesRepository.Companion.getInstance(dataStore)
}
actual fun createAuthRepository(userAuthPreferencesRepository: UserAuthPreferencesRepository): AuthRepository {
    return RetrofitClient.createAuthRepository(userAuthPreferencesRepository);
}

actual fun createAnalyzerRepository(): AnalyzerRepository{
    return DefaultAnalyzerRepository()
}

actual fun createNoteApiService(): NoteApiService? {
    return RetrofitClient.createNoteApiService()
}

actual fun createGroupApiService(): GroupApiService? {
    return RetrofitClient.createGroupApiService()
}

actual fun createNotificationService(): NotificationService {
    // Получаем контекст из инициализированного applicationContext
    val context = org.example.project.getApplicationContext()
    val notificationHelper = NotificationHelper(context)
    notificationHelper.ensureChannel()
    return AndroidNotificationService(context, notificationHelper)
}

