package org.example.project.data.network

import androidx.compose.animation.SharedTransitionScope
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


interface NoteApiService {

    suspend fun getAllTasks(userId: Long) : Result<List<TaskDto>>

    suspend fun getTaskDetails(taskId: Long) : Result<TaskDetailsDto>

    suspend fun createTask(createTaskRequest: CreateTaskRequest) : Result<TaskDto>

    suspend fun updateTask(taskId: Long, updateTaskRequest: UpdateTaskRequest) : Result<TaskDto>

    suspend fun deleteTask(taskId: Long) : Result<UInt>

    suspend fun addCommentToTask(taskId: Long, commentRequest: AddCommentRequest): Result<CommentDto>

    suspend fun deleteCommentFromTask(commentId: Long) : Result<TaskDto>

    suspend fun getAllGroupTask(groupId: Long) : Result<List<TaskDto>>


    suspend fun getAllNotes(userId: Long) : Result<List<NoteDto>>

    suspend fun  getNoteDetails(noteId: Long) : Result<NoteDetailsDto>

    suspend fun createNote(createNoteRequest: CreateNoteRequest) : Result<NoteDto>

    suspend fun updateNote(noteId: Long, updateNoteRequest: UpdateNoteRequest) : Result<NoteDto>

    suspend fun  deleteNote(noteId: Long) : Result<UInt>

    suspend fun addCommentToNote(noteId: Long, commentRequest: AddCommentRequest) : Result<CommentDto>

    suspend fun  deleteCommentFromNote(commentId: Long) : Result<NoteDto>

    suspend fun getAllGroupNotes(groupId: Long) : Result<List<NoteDto>>

}

