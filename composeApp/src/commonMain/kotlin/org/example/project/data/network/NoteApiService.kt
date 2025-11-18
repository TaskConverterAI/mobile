package org.example.project.data.network

import org.example.project.data.network.models.NoteDto
import org.example.project.data.network.models.SyncRequest
import org.example.project.data.network.models.SyncResponse

/**
 * Интерфейс для работы с API заметок
 * Платформенные реализации будут использовать Retrofit (Android) или другие HTTP клиенты
 */
interface NoteApiService {
    /**
     * Получить все заметки с сервера
     */
    suspend fun getAllNotes(): Result<List<NoteDto>>

    /**
     * Получить заметку по ID
     */
    suspend fun getNoteById(noteId: Long): Result<NoteDto>

    /**
     * Создать новую заметку на сервере
     */
    suspend fun createNote(note: NoteDto): Result<NoteDto>

    /**
     * Обновить заметку на сервере
     */
    suspend fun updateNote(noteId: Long, note: NoteDto): Result<NoteDto>

    /**
     * Удалить заметку на сервере
     */
    suspend fun deleteNote(noteId: Long): Result<Unit>

    /**
     * Синхронизировать заметки с сервером
     * Отправляет локальные изменения и получает обновления с сервера
     */
    suspend fun syncNotes(request: SyncRequest): Result<SyncResponse>
}

