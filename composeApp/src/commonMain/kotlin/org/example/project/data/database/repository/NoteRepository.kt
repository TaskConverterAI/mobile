package org.example.project.data.database.repository

import coil3.util.Logger
import kotlinx.coroutines.flow.Flow
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Note
import org.example.project.data.commonData.toAddCommentRequest
import org.example.project.data.commonData.toComment
import org.example.project.data.commonData.toCreateNoteRequest
import org.example.project.data.commonData.toNote
import org.example.project.data.commonData.toUpdateNoteRequest
import org.example.project.data.database.AppDatabase
import org.example.project.data.network.NoteApiService
import org.example.project.data.network.models.AddCommentRequest
import kotlin.time.ExperimentalTime

class NoteRepository(
    private val database: AppDatabase,
    private val noteApiService: NoteApiService? = null
) {

    private val noteDao = database.noteDao()
    private val groupDao = database.groupDao()

//    // Sync manager будет инициализирован при необходимости
//    private var syncManager: NoteSyncManager? = null
//
//    /**
//     * Установить sync manager для работы с удалённым сервером
//     */
//    fun setSyncManager(manager: NoteSyncManager) {
//        syncManager = manager
//    }

    suspend fun getGroupNoteCount(groupId: Long) : Int {
        val result = noteApiService?.getAllGroupNotes(groupId = groupId)
        val retVal = result?.fold(
            onSuccess = {response -> response.size},
            onFailure = {response -> 0}
        )
        if (retVal != null){
            return retVal
        }

        return 0
    }
    

    @OptIn(ExperimentalTime::class)
    suspend fun getAllNotes(userId: Long): List<Note>? {

        val result =  noteApiService?.getAllNotes(userId)
        co.touchlab.kermit.Logger.i {result.toString()}
        val retVal = result?.fold(
            onSuccess = { response ->
                response.map { noteDto -> noteDto.toNote() }
            },
            onFailure = { error ->
                error.printStackTrace()
                null
            }
        )
//        return noteDao.getAllNotesWithTasks().map { notesWithTasks ->
//            notesWithTasks.map { noteWithTasks ->
//                val comments = noteDao.getCommentsForNote(noteWithTasks.note.id)
//                    .map { it.toComment() }
//                noteWithTasks.toNote(comments)
//            }
//        }
        return retVal
    }

    /**
     * Получить заметку по ID с полными деталями
     */
    @OptIn(ExperimentalTime::class)
    suspend fun getNoteById(noteId: Long): Note? {
        val result =noteApiService?.getNoteDetails(noteId)
        val retVal = result?.fold(
            onSuccess = { response ->
                response.toNote()
            },
            onFailure = { error ->
                error.printStackTrace()
                null
            }
        )
//        val noteWithTasks = noteDao.getNoteWithTasks(noteId) ?: return null
//        val comments = noteDao.getCommentsForNote(noteId).map { it.toComment() }
//        return noteWithTasks.toNote(comments)
        return retVal
    }

    /**
     * Получить заметки по группе
     */
    @OptIn(ExperimentalTime::class)
    suspend fun getNotesByGroup(groupId: Long): List<Note>? {
        val result =  noteApiService?.getAllGroupNotes(groupId)
        val retVal = result?.fold(
            onSuccess = { response ->
                response.map({noteDto -> noteDto.toNote()})
            },
            onFailure = { error ->
                error.printStackTrace()
                null
            }
        )
//        return noteDao.getNotesByGroupWithTasks(groupId).map { notesWithTasks ->
//            notesWithTasks.map { noteWithTasks ->
//                val comments = noteDao.getCommentsForNote(noteWithTasks.note.id)
//                    .map { it.toComment() }
//                noteWithTasks.toNote(comments)
//            }
//        }
        return retVal
    }

    /**
     * Вставить новую заметку
     * @param note - заметка с группой (объект Group будет преобразован в groupId)
     * @return ID новой заметки
     */
    @OptIn(ExperimentalTime::class)
    suspend fun insertNote(userId: Long, note: Note): Note? {

        val result = noteApiService?.createNote(note.toCreateNoteRequest())
        val retVal = result?.fold(
            onSuccess = {response ->
                response.toNote()
            },

            onFailure = {
                error ->
                error.printStackTrace()
                null
            }
        )

//        val noteEntity = note.toEntity()
//        val noteId = noteDao.insertNote(noteEntity)
//
//        note.comments.forEach { comment ->
//            noteDao.insertComment(comment.toNoteCommentEntity(noteId))
//        }
//
//        return noteId
        return retVal
    }

    /**
     * Обновить заметку
     */
    @OptIn(ExperimentalTime::class)
    suspend fun updateNote(note: Note) {

        val result = noteApiService?.updateNote(note.id,
            note.toUpdateNoteRequest())
        val retVal = result?.fold(
            onSuccess = {response ->
                response.toNote()
            },

            onFailure = {
                    error ->
                error.printStackTrace()
                null
            }
        )

//        val noteEntity = note.toEntity()
//        noteDao.updateNote(noteEntity)


    }

    /**
     * Удалить заметку
     */
    suspend fun deleteNote(noteId: Long) {
        val result = noteApiService?.deleteNote(noteId)
        //noteDao.deleteNoteById(noteId)
    }

    /**
     * Добавить комментарий к заметке
     */
    @OptIn(ExperimentalTime::class)
    suspend fun addCommentToNote(noteId: Long, comment: Comment) : Comment? {
        val commentRequest = AddCommentRequest(
            authorId = comment.author,
            text = comment.content
        )
        val result = noteApiService?.addCommentToNote(noteId, comment.toAddCommentRequest())
        val retVal = result?.fold(
            onSuccess = {response ->
                response.toComment()
            },

            onFailure = {
                error ->
                error.printStackTrace()
                null
            }
        )
        //noteDao.insertComment(comment.toNoteCommentEntity(noteId))
        return retVal
    }

    suspend fun deleteCommentFromNote(commentId: Long) {
        val result = noteApiService?.deleteCommentFromNote(commentId)
    }

    /**
     * Получить комментарии для заметки
     */
//    suspend fun getCommentsForNote(noteId: Long): List<Comment> {
//        return noteDao.getCommentsForNote(noteId).map { it.toComment() }
//    }

//    /**
//     * Найти группу по имени или создать новую
//     * @return ID группы
//     */
//    private suspend fun findOrCreateGroup(group: org.example.project.data.commonData.Group): String? {
//        // Если группа дефолтная
//        if (group.name == "Без группы" || group.id.isEmpty() && group.name.isEmpty()) {
//            return null
//        }
//
//        // Если у группы уже есть ID, используем его
//        if (group.id.isNotEmpty()) {
//            return group.id
//        }
//
//        // Ищем группу по имени
//        val allGroups = groupDao.getAllGroups()
//        var groupId: String? = null
//
//        allGroups.collect { groups ->
//            groupId = groups.find { it.name == group.name }?.id
//        }
//
//        // Если группа не найдена, создаём новую
//        if (groupId == null) {
//            val newGroup = group.toEntity()
//            groupDao.insert(newGroup)
//            groupId = newGroup.id
//        }
//
//        return groupId
//    }

    // ==================== Методы синхронизации ====================

//    /**
//     * Синхронизировать все заметки с сервером
//     */
//    suspend fun syncWithServer(): Result<Unit> {
//        return syncManager?.syncWithServer()
//            ?: Result.failure(Exception("Sync manager not initialized"))
//    }
//
//    /**
//     * Загрузить заметку с сервера
//     */
//    suspend fun pullNoteFromServer(noteId: Long): Result<Unit> {
//        return syncManager?.pullNoteFromServer(noteId)
//            ?: Result.failure(Exception("Sync manager not initialized"))
//    }
//
//    /**
//     * Отправить заметку на сервер
//     */
//    suspend fun pushNoteToServer(noteId: Long): Result<Unit> {
//        return syncManager?.pushNoteToServer(noteId)
//            ?: Result.failure(Exception("Sync manager not initialized"))
//    }
//
//    /**
//     * Получить состояние синхронизации
//     */
//    fun getSyncState(): Flow<org.example.project.data.sync.SyncState>? {
//        return syncManager?.syncState
//    }
//
//    /**
//     * Вставить заметку и сразу синхронизировать с сервером
//     */
////    suspend fun insertNoteAndSync(note: Note): Long {
////        val noteId = insertNote(note)
////        syncManager?.pushNoteToServer(noteId)
////        return noteId
////    }
//
//    /**
//     * Обновить заметку и сразу синхронизировать с сервером
//     */
//    suspend fun updateNoteAndSync(noteId: Long, note: Note) {
//        updateNote(note)
//        syncManager?.pushNoteToServer(noteId)
//    }
//
//    /**
//     * Удалить заметку и сразу синхронизировать с сервером
//     */
//    suspend fun deleteNoteAndSync(noteId: Long) {
//        deleteNote(noteId)
//        noteApiService?.deleteNote(noteId)
//    }
}

