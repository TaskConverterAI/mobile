package org.example.project.network

import org.example.project.data.network.models.AddCommentRequest
import org.example.project.data.network.models.CommentDto
import org.example.project.data.network.models.CreateNoteRequest
import org.example.project.data.network.models.CreateTaskRequest
import org.example.project.data.network.models.NoteDetailsDto
import org.example.project.data.network.models.NoteDto
import org.example.project.data.network.models.TaskDetailsDto
import org.example.project.data.network.models.TaskDto
import org.example.project.data.network.models.UpdateNoteRequest
import org.example.project.data.network.models.UpdateTaskRequest

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API интерфейс для работы с заметками
 */
interface RetrofitNoteApiService {

    @GET("tasks/personal/{userId}")
    suspend fun getAllTasks(@Path("userId") userId: Long) : Response<List<TaskDto>>

    @GET("task/details/{taskId}")
    suspend fun getTaskDetails(@Path("taskId") taskId: Long) : Response<TaskDetailsDto>

    @GET("task/group/{groupId}")
    suspend fun getAllGroupTasks(@Path("groupId") groupId: Long) : Response<List<TaskDto>>

    @POST("tasks")
    suspend fun createTask(@Body createTaskRequest: CreateTaskRequest) : Response<TaskDto>

    @PUT("tasks/{taskId}")
    suspend fun updateTask(@Path("taskId") taskId: Long, @Body updateTaskRequest: UpdateTaskRequest) : Response<TaskDto>

    @DELETE("tasks/{taskId}")
    suspend fun deleteTask(@Path("taskId") taskId: Long) : Response<UInt>

    @PUT("task/{taskId}/comment")
    suspend fun addCommentToTask(@Path("taskId") taskId: Long, @Body commentRequest: AddCommentRequest) : Response<CommentDto>

    @DELETE("task/comment/{commentId}")
    suspend fun deleteCommentFromTask(@Path("commentId") commentId: Long) : Response<TaskDto>

    @GET("tasks/note/personal/{userId")
    suspend fun getAllNotes(@Path("userId") userId: Long): Response<List<NoteDto>>

    @GET("tasks/note/details/{noteId}")
    suspend fun getNoteDetails(@Path("noteId") noteId: Long) : Response<NoteDetailsDto>

    @GET("tasks/note/group/{groupId")
    suspend fun getAllGroupNotes(@Path("groupId") groupId: Long) : Response<List<NoteDto>>

    @POST("tasks/note")
    suspend fun createNote(@Body createNoteRequest: CreateNoteRequest) : Response<NoteDto>

    @PUT("tasks/note/{nodeId}")
    suspend fun updateNote(@Path("noteId") noteId: Long, @Body updateNoteRequest: UpdateNoteRequest): Response<NoteDto>

    @DELETE("tasks/note/{noteId}")
    suspend fun deleteNote(@Path("noteId") noteId: Long) : Response<UInt>

    @PUT("task/note/{noteId}/comment")
    suspend fun addCommentToNote(@Path("noteId") noteId: Long, @Body commentRequest: AddCommentRequest) : Response<CommentDto>

    @DELETE("task/note/comment/{commentId}")
    suspend fun deleteCommentFromNote(@Path("commentId") commentId: Long) : Response<NoteDto>


}

