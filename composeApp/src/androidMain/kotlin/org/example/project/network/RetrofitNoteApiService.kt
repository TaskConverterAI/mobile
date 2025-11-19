package org.example.project.network

import org.example.project.data.network.models.NoteDto
import org.example.project.data.network.models.SyncRequest
import org.example.project.data.network.models.SyncResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API интерфейс для работы с заметками
 */
interface RetrofitNoteApiService {
    @GET("notes")
    suspend fun getAllNotes(): Response<List<NoteDto>>

    @GET("notes/{id}")
    suspend fun getNoteById(@Path("id") noteId: Long): Response<NoteDto>

    @POST("notes")
    suspend fun createNote(@Body note: NoteDto): Response<NoteDto>

    @PUT("notes/{id}")
    suspend fun updateNote(
        @Path("id") noteId: Long,
        @Body note: NoteDto
    ): Response<NoteDto>

    @DELETE("notes/{id}")
    suspend fun deleteNote(@Path("id") noteId: Long): Response<Unit>

    @POST("notes/sync")
    suspend fun syncNotes(@Body request: SyncRequest): Response<SyncResponse>
}

