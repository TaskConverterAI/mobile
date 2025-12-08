package org.example.project.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

import org.example.project.data.auth.AuthRepository
import org.example.project.data.analyzer.AnalyzerRepository
import org.example.project.data.auth.UserAuthPreferencesRepository
import org.example.project.data.database.repository.NoteRepository
import org.example.project.data.database.repository.TaskRepository
import org.example.project.data.database.repository.GroupRepository

interface AppContainer {
    val userAuthPreferencesRepository: UserAuthPreferencesRepository
    val authRepository: AuthRepository
    val analyzerRepository: AnalyzerRepository
    val noteRepository: NoteRepository
    val taskRepository: TaskRepository
    val groupRepository: GroupRepository
}

// Expect function to create platform-specific AuthRepository
expect fun createAuthPreferencesRepository(dataStore: DataStore<Preferences>): UserAuthPreferencesRepository
expect fun createAuthRepository(userAuthPreferencesRepository: UserAuthPreferencesRepository): AuthRepository

expect fun createAnalyzerRepository(): AnalyzerRepository

// Expect function to create platform-specific NoteApiService (optional - can be null if offline only)
expect fun createNoteApiService(): org.example.project.data.network.NoteApiService?

expect fun createGroupApiService(): org.example.project.data.network.GroupApiService?

class DefaultAppContainer(
    dataStore: DataStore<Preferences>,
    database: org.example.project.data.database.AppDatabase
) : AppContainer {
    override val userAuthPreferencesRepository: UserAuthPreferencesRepository =
        createAuthPreferencesRepository(dataStore)

    override val authRepository: AuthRepository =
        createAuthRepository(userAuthPreferencesRepository)

    override val analyzerRepository: AnalyzerRepository = createAnalyzerRepository()

    // Создать API сервис для синхронизации (может быть null для offline режима)
    private val noteApiService = createNoteApiService()

    // Создать репозитории
    override val noteRepository: NoteRepository = NoteRepository(database, noteApiService)
    override val taskRepository: TaskRepository = TaskRepository(database)

    // Создать sync preferences
    private val syncPreferences = org.example.project.data.sync.SyncPreferences(dataStore)

    // Создать CoroutineScope для фоновых операций синхронизации
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

//    // Создать sync manager если есть API сервис
//    private val syncManager = if (noteApiService != null) {
//        org.example.project.data.sync.NoteSyncManager(
//            noteRepository = noteRepository,
//            noteApiService = noteApiService,
//            syncPreferences = syncPreferences,
//            coroutineScope = syncScope,
//            autoStart = false
//        )
//    } else null

//    init {
//        // Установить sync manager в репозиторий если он доступен
//        syncManager?.let { noteRepository.setSyncManager(it) }
//    }

    override val groupRepository: GroupRepository = GroupRepository(createGroupApiService())
}
