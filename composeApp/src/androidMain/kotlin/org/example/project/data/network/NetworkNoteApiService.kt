package org.example.project.data.network

import org.example.project.data.network.models.NoteDto
import org.example.project.data.network.models.SyncRequest
import org.example.project.data.network.models.SyncResponse
import org.example.project.network.RetrofitNoteApiService

/**
 * Android реализация NoteApiService с использованием Retrofit
 */
class NetworkNoteApiService(
    private val retrofitService: RetrofitNoteApiService
) : NoteApiService {

    override suspend fun getAllNotes(): Result<List<NoteDto>> {
        return try {
            val response = retrofitService.getAllNotes()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch notes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNoteById(noteId: Long): Result<NoteDto> {
        return try {
            val response = retrofitService.getNoteById(noteId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch note: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createNote(note: NoteDto): Result<NoteDto> {
        return try {
            val response = retrofitService.createNote(note)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create note: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNote(noteId: Long, note: NoteDto): Result<NoteDto> {
        return try {
            val response = retrofitService.updateNote(noteId, note)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update note: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(noteId: Long): Result<Unit> {
        return try {
            val response = retrofitService.deleteNote(noteId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete note: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncNotes(request: SyncRequest): Result<SyncResponse> {
        return try {
            val response = retrofitService.syncNotes(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to sync notes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

