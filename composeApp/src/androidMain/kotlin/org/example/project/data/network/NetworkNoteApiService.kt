package org.example.project.data.network

import co.touchlab.kermit.Logger
import kotlinx.datetime.LocalDate
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
import org.example.project.network.RetrofitClient
import org.example.project.network.RetrofitNoteApiService

/**
 * Android реализация NoteApiService с использованием Retrofit
 */
class NetworkNoteApiService(
    private val retrofitService: RetrofitNoteApiService
) : NoteApiService {

    private val logger = Logger.withTag("NetworkNoteApiService")

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
            println("========== GET TASK DETAILS API CALL ==========")
            println("Base URL: ${RetrofitClient.taskBaseUrl()}")
            println("TaskId: $taskId")

            val response = retrofitService.getTaskDetails(taskId)

            println("HTTP Response:")
            println("  - Status code: ${response.code()}")
            println("  - Is successful: ${response.isSuccessful}")
            println("  - Message: ${response.message()}")
            println("  - Body: ${response.body()}")
            val errorBodyStr = try {
                response.errorBody()?.string()
            } catch (e: Exception) { e.message }
            println("  - Error body: $errorBodyStr")

            if (response.isSuccessful && response.body() != null) {
                println("✅ getTaskDetails success: ${response.body()}")
                println("========== END GET TASK DETAILS API CALL ==========")
                Result.success(response.body()!!)
            } else {
                val errorMsg = (errorBodyStr?.takeIf { it.isNotBlank() }) ?: response.message()
                println("❌ getTaskDetails FAILED! Error: $errorMsg")
                println("========== END GET TASK DETAILS API CALL ==========")
                Result.failure(Exception("HTTP ${response.code()}: $errorMsg"))
            }
        } catch (e: Exception) {
            println("❌ Exception during getTaskDetails!")
            println("Exception type: ${e::class.simpleName}")
            println("Exception message: ${e.message}")
            println("Stack trace: ${e.stackTraceToString()}")
            println("========== END GET TASK DETAILS API CALL ==========")
            Result.failure(e)
        }
    }

    override suspend fun createTask(createTaskRequest: CreateTaskRequest): Result<TaskDto> {
        return try {
            println("========== CREATE TASK API CALL ==========")
            println("Base URL: ${RetrofitClient.taskBaseUrl()}")
            println("Request body: $createTaskRequest")
            println("  - Title: ${createTaskRequest.title}")
            println("  - Description: ${createTaskRequest.description}")
            println("  - AuthorId: ${createTaskRequest.authorId}")
            println("  - DoerId: ${createTaskRequest.doerId}")
            println("  - Priority: ${createTaskRequest.priority}")
            println("  - GroupId: ${createTaskRequest.groupId}")
            println("  - Location: ${createTaskRequest.location}")
            println("  - Deadline: ${createTaskRequest.deadline}")

            val response = retrofitService.createTask(createTaskRequest)

            println("HTTP Response:")
            println("  - Status code: ${response.code()}")
            println("  - Is successful: ${response.isSuccessful}")
            println("  - Message: ${response.message()}")
            println("  - Body: ${response.body()}")
            println("  - Error body: ${response.errorBody()?.string()}")

            if (response.isSuccessful && response.body() != null) {
                println("✅ Task created successfully!")
                println("Response DTO: ${response.body()}")
                println("========== END CREATE TASK API CALL ==========")
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                println("❌ Task creation FAILED!")
                println("Error: $errorMsg")
                println("========== END CREATE TASK API CALL ==========")
                Result.failure(Exception("HTTP ${response.code()}: $errorMsg"))
            }
        } catch (e: Exception) {
            println("❌ Exception during task creation!")
            println("Exception type: ${e::class.simpleName}")
            println("Exception message: ${e.message}")
            println("Stack trace: ${e.stackTraceToString()}")
            println("========== END CREATE TASK API CALL ==========")
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
                val body = response.body()!!
                logger.i { "getAllNotes: success code=${response.code()} count=${body.size}" }
                Result.success(body)
            } else {
                logger.e { "getAllNotes: http error code=${response.code()} msg=${response.message()}" }
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            logger.e(e) { "getAllNotes: exception ${e.message}" }
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
                logger.i { "createNote: success code=${response.code()}" }
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = "HTTP ${response.code()}: ${response.message()}"
                logger.e {
                    "createNote: http error\n" +
                    "  code=${response.code()}\n" +
                    "  message=${response.message()}\n" +
                    "  errorBody=$errorBody"
                }
                Result.failure(Exception("$errorMsg - $errorBody"))
            }
        } catch (e: Exception) {
            logger.e(e) { "createNote: exception ${e.message}\n${e.stackTraceToString()}" }
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
            val response = retrofitService.getAllGroupNotes(groupId)
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
