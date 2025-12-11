package org.example.project.data.network

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
import org.example.project.network.RetrofitNoteApiService

/**
 * Android реализация NoteApiService с использованием Retrofit
 */
class NetworkNoteApiService(
    private val retrofitService: RetrofitNoteApiService
) : NoteApiService {
    override suspend fun getAllTasks(userId: Long): Result<List<TaskDto>> {
        return try {
            val response = retrofitService.getAllTasks(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTaskDetails(taskId: Long): Result<TaskDetailsDto> {
        return try {
            val response = retrofitService.getTaskDetails(taskId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTask(createTaskRequest: CreateTaskRequest): Result<TaskDto> {
        return try {
            val response = retrofitService.createTask(createTaskRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTask(
        taskId: Long,
        updateTaskRequest: UpdateTaskRequest
    ): Result<TaskDto> {
        return try {
            val response = retrofitService.updateTask(taskId, updateTaskRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(taskId: Long): Result<UInt> {
        return try {
            val response = retrofitService.deleteTask(taskId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllGroupTask(groupId: Long): Result<List<TaskDto>> {
        return try {
            val response = retrofitService.getAllGroupTasks(groupId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addCommentToTask(
        taskId: Long,
        commentRequest: AddCommentRequest
    ): Result<CommentDto> {
        return try {
            val response = retrofitService.addCommentToTask(taskId, commentRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCommentFromTask(commentId: Long): Result<TaskDto> {
        return try {
            val response = retrofitService.deleteCommentFromTask(commentId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getAllNotes(userId: Long): Result<List<NoteDto>> {
        return try {
            val response = retrofitService.getAllNotes(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNoteDetails(noteId: Long): Result<NoteDetailsDto> {
        return try {
            val response = retrofitService.getNoteDetails(noteId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createNote(createNoteRequest: CreateNoteRequest): Result<NoteDto> {
        return try {

            val response = retrofitService.createNote(createNoteRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNote(noteId: Long, updateNoteRequest: UpdateNoteRequest): Result<NoteDto> {
        return try {
            val response = retrofitService.updateNote(noteId, updateNoteRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(noteId: Long): Result<UInt> {
        return try {
            val response = retrofitService.deleteNote(noteId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllGroupNotes(groupId: Long): Result<List<NoteDto>> {

        return try {
            val response = retrofitService.getAllNotes(groupId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addCommentToNote(
        noteId: Long,
        commentRequest: AddCommentRequest
    ): Result<CommentDto> {

        return try {
            val response = retrofitService.addCommentToNote(noteId, commentRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCommentFromNote(commentId: Long): Result<NoteDto> {

        return try {
            val response = retrofitService.deleteCommentFromNote(commentId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

