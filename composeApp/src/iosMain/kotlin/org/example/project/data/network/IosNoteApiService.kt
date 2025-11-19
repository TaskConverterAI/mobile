package org.example.project.data.network

import org.example.project.data.network.models.NoteDto
import org.example.project.data.network.models.SyncRequest
import org.example.project.data.network.models.SyncResponse

/**
 * iOS реализация NoteApiService
 * TODO: Реализовать с использованием платформенных HTTP клиентов (например, URLSession)
 */
class IosNoteApiService : NoteApiService {

    override suspend fun getAllNotes(): Result<List<NoteDto>> {
        // TODO: Реализовать HTTP запрос для iOS
        return Result.failure(NotImplementedError("iOS implementation not yet available"))
    }

    override suspend fun getNoteById(noteId: Long): Result<NoteDto> {
        // TODO: Реализовать HTTP запрос для iOS
        return Result.failure(NotImplementedError("iOS implementation not yet available"))
    }

    override suspend fun createNote(note: NoteDto): Result<NoteDto> {
        // TODO: Реализовать HTTP запрос для iOS
        return Result.failure(NotImplementedError("iOS implementation not yet available"))
    }

    override suspend fun updateNote(noteId: Long, note: NoteDto): Result<NoteDto> {
        // TODO: Реализовать HTTP запрос для iOS
        return Result.failure(NotImplementedError("iOS implementation not yet available"))
    }

    override suspend fun deleteNote(noteId: Long): Result<Unit> {
        // TODO: Реализовать HTTP запрос для iOS
        return Result.failure(NotImplementedError("iOS implementation not yet available"))
    }

    override suspend fun syncNotes(request: SyncRequest): Result<SyncResponse> {
        // TODO: Реализовать HTTP запрос для iOS
        return Result.failure(NotImplementedError("iOS implementation not yet available"))
    }
}
