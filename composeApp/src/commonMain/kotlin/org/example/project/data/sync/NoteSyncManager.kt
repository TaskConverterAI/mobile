package org.example.project.data.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.project.data.network.NoteApiService
import org.example.project.data.network.models.SyncRequest
import org.example.project.data.database.repository.NoteRepository
import org.example.project.data.sync.mappers.toDto
import org.example.project.data.sync.mappers.toNote
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Состояние синхронизации
 */
sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Success(val syncedCount: Int) : SyncState()
    data class Error(val message: String) : SyncState()
}

/**
 * Менеджер синхронизации заметок с сервером
 * Автоматически выполняет периодическую синхронизацию с сервером
 *
 * @param noteRepository Репозиторий заметок
 * @param noteApiService API сервис для работы с заметками
 * @param syncPreferences Хранилище настроек синхронизации
 * @param coroutineScope Scope для запуска корутин
 * @param syncInterval Интервал между синхронизациями (по умолчанию 15 минут)
 * @param autoStart Автоматически запустить синхронизацию при создании (по умолчанию true)
 */
class NoteSyncManager(
    private val noteRepository: NoteRepository,
    private val noteApiService: NoteApiService,
    private val syncPreferences: SyncPreferences,
    private val coroutineScope: CoroutineScope,
    private val syncInterval: Duration = 15.minutes,
    autoStart: Boolean = true
) {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: Flow<SyncState> = _syncState.asStateFlow()

    private var syncJob: Job? = null
    private val _isRunning = MutableStateFlow(false)
    val isRunning: Flow<Boolean> = _isRunning.asStateFlow()

    init {
        if (autoStart) {
            start()
        }
    }

    /**
     * Запустить автоматическую периодическую синхронизацию
     */
    fun start() {
        if (_isRunning.value) {
            return // Уже запущена
        }

        _isRunning.value = true
        syncJob = coroutineScope.launch {
            while (isActive && _isRunning.value) {
                syncWithServer()
                delay(syncInterval)
            }
        }
    }

    /**
     * Остановить автоматическую синхронизацию
     */
    fun stop() {
        _isRunning.value = false
        syncJob?.cancel()
        syncJob = null
    }

    /**
     * Выполнить полную синхронизацию с сервером
     */
    suspend fun syncWithServer(): Result<Unit> {
        return try {
            _syncState.value = SyncState.Syncing

            val lastSyncTimestamp = syncPreferences.getLastSyncTimestamp()

            // Получаем все локальные заметки
            val localNotes = mutableListOf<org.example.project.data.commonData.Note>()
            noteRepository.getAllNotes().collect { notes ->
                localNotes.clear()
                localNotes.addAll(notes)
            }

            // Конвертируем в DTO
            val noteDtos = localNotes.map { it.toDto() }

            // Отправляем запрос на синхронизацию
            val syncRequest = SyncRequest(
                lastSyncTimestamp = lastSyncTimestamp,
                notes = noteDtos
            )

            val result = noteApiService.syncNotes(syncRequest)

            result.fold(
                onSuccess = { syncResponse ->
                    if (syncResponse.success) {
                        // Применяем изменения с сервера
                        applySyncResponse(syncResponse)

                        // Сохраняем timestamp синхронизации
                        syncPreferences.setLastSyncTimestamp(syncResponse.syncTimestamp)

                        _syncState.value = SyncState.Success(syncResponse.notes.size)
                        Result.success(Unit)
                    } else {
                        val errorMsg = syncResponse.message ?: "Sync failed"
                        _syncState.value = SyncState.Error(errorMsg)
                        Result.failure(Exception(errorMsg))
                    }
                },
                onFailure = { error ->
                    _syncState.value = SyncState.Error(error.message ?: "Unknown error")
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    /**
     * Применить изменения, полученные с сервера
     */
    private suspend fun applySyncResponse(syncResponse: org.example.project.data.network.models.SyncResponse) {
        // Удаляем заметки, которые были удалены на сервере
        syncResponse.deletedNoteIds.forEach { noteId ->
            noteRepository.deleteNote(noteId)
        }

        // Обновляем или создаём заметки с сервера
        syncResponse.notes.forEach { noteDto ->
            val note = noteDto.toNote()

            if (noteDto.id != null && noteDto.id > 0) {
                // Проверяем, существует ли заметка локально
                val existingNote = noteRepository.getNoteById(noteDto.id)
                if (existingNote != null) {
                    // Обновляем существующую заметку
                    noteRepository.updateNote(noteDto.id, note)
                } else {
                    // Создаём новую заметку (сервер вернул заметку, которой нет локально)
                    noteRepository.insertNote(note)
                }
            } else {
                // Новая заметка с сервера
                noteRepository.insertNote(note)
            }
        }
    }

    /**
     * Загрузить заметку с сервера
     */
    suspend fun pullNoteFromServer(noteId: Long): Result<Unit> {
        return try {
            val result = noteApiService.getNoteById(noteId)

            result.fold(
                onSuccess = { noteDto ->
                    val note = noteDto.toNote()
                    val existingNote = noteRepository.getNoteById(noteId)

                    if (existingNote != null) {
                        noteRepository.updateNote(noteId, note)
                    } else {
                        noteRepository.insertNote(note)
                    }

                    Result.success(Unit)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Отправить заметку на сервер
     */
    suspend fun pushNoteToServer(noteId: Long): Result<Unit> {
        return try {
            val note = noteRepository.getNoteById(noteId)
                ?: return Result.failure(Exception("Note not found"))

            val noteDto = note.toDto()

            val result = if (noteId > 0) {
                noteApiService.updateNote(noteId, noteDto)
            } else {
                noteApiService.createNote(noteDto)
            }

            result.fold(
                onSuccess = { updatedNoteDto ->
                    // Обновляем локальную заметку с ID с сервера (если это была новая заметка)
                    if (updatedNoteDto.id != null && updatedNoteDto.id != noteId) {
                        val updatedNote = updatedNoteDto.toNote()
                        noteRepository.insertNote(updatedNote)
                    }
                    Result.success(Unit)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Сбросить состояние синхронизации
     */
    fun resetSyncState() {
        _syncState.value = SyncState.Idle
    }
}

