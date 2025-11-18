package org.example.project.data.database.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Note
import org.example.project.data.database.AppDatabase
import org.example.project.data.database.mappers.toComment
import org.example.project.data.database.mappers.toEntity
import org.example.project.data.database.mappers.toNote
import org.example.project.data.database.mappers.toNoteCommentEntity
import org.example.project.data.network.NoteApiService
import org.example.project.data.sync.NoteSyncManager

class NoteRepository(
    private val database: AppDatabase,
    private val noteApiService: NoteApiService? = null
) {

    private val noteDao = database.noteDao()
    private val groupDao = database.groupDao()

    // Sync manager будет инициализирован при необходимости
    private var syncManager: NoteSyncManager? = null

    /**
     * Установить sync manager для работы с удалённым сервером
     */
    fun setSyncManager(manager: NoteSyncManager) {
        syncManager = manager
    }

    /**
     * Получить все заметки с группами и задачами
     */
    fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotesWithTasks().map { notesWithTasks ->
            notesWithTasks.map { noteWithTasks ->
                // Получаем комментарии для каждой заметки
                val comments = noteDao.getCommentsForNote(noteWithTasks.note.id)
                    .map { it.toComment() }
                noteWithTasks.toNote(comments)
            }
        }
    }

    /**
     * Получить заметку по ID с полными деталями
     */
    suspend fun getNoteById(noteId: Long): Note? {
        val noteWithTasks = noteDao.getNoteWithTasks(noteId) ?: return null
        val comments = noteDao.getCommentsForNote(noteId).map { it.toComment() }
        return noteWithTasks.toNote(comments)
    }

    /**
     * Получить заметки по группе
     */
    fun getNotesByGroup(groupId: Long): Flow<List<Note>> {
        return noteDao.getNotesByGroupWithTasks(groupId).map { notesWithTasks ->
            notesWithTasks.map { noteWithTasks ->
                val comments = noteDao.getCommentsForNote(noteWithTasks.note.id)
                    .map { it.toComment() }
                noteWithTasks.toNote(comments)
            }
        }
    }

    /**
     * Вставить новую заметку
     * @param note - заметка с группой (объект Group будет преобразован в groupId)
     * @return ID новой заметки
     */
    suspend fun insertNote(note: Note): Long {
        // Найти или создать группу
        val groupId = findOrCreateGroup(note.group)

        // Вставить заметку с groupId
        val noteEntity = note.toEntity(groupId = groupId)
        val noteId = noteDao.insertNote(noteEntity)

        // Вставить комментарии
        note.comments.forEach { comment ->
            noteDao.insertComment(comment.toNoteCommentEntity(noteId))
        }

        return noteId
    }

    /**
     * Обновить заметку
     */
    suspend fun updateNote(noteId: Long, note: Note) {
        // Найти или создать группу
        val groupId = findOrCreateGroup(note.group)

        // Обновить заметку с новым groupId
        val noteEntity = note.toEntity(id = noteId, groupId = groupId)
        noteDao.updateNote(noteEntity)
    }

    /**
     * Удалить заметку
     */
    suspend fun deleteNote(noteId: Long) {
        noteDao.deleteNoteById(noteId)
    }

    /**
     * Добавить комментарий к заметке
     */
    suspend fun addCommentToNote(noteId: Long, comment: Comment) {
        noteDao.insertComment(comment.toNoteCommentEntity(noteId))
    }

    /**
     * Получить комментарии для заметки
     */
    suspend fun getCommentsForNote(noteId: Long): List<Comment> {
        return noteDao.getCommentsForNote(noteId).map { it.toComment() }
    }

    /**
     * Найти группу по имени или создать новую
     * @return ID группы
     */
    private suspend fun findOrCreateGroup(group: org.example.project.data.commonData.Group): Long? {
        // Если группа дефолтная
        if (group.name == "Без группы" || group.id == 0L && group.name.isEmpty()) {
            return null
        }

        // Если у группы уже есть ID, используем его
        if (group.id != 0L) {
            return group.id
        }

        // Ищем группу по имени
        val allGroups = groupDao.getAllGroups()
        var groupId: Long? = null

        allGroups.collect { groups ->
            groupId = groups.find { it.name == group.name }?.id
        }

        // Если группа не найдена, создаём новую
        if (groupId == null) {
            groupId = groupDao.insert(group.toEntity())
        }

        return groupId
    }

    // ==================== Методы синхронизации ====================

    /**
     * Синхронизировать все заметки с сервером
     */
    suspend fun syncWithServer(): Result<Unit> {
        return syncManager?.syncWithServer()
            ?: Result.failure(Exception("Sync manager not initialized"))
    }

    /**
     * Загрузить заметку с сервера
     */
    suspend fun pullNoteFromServer(noteId: Long): Result<Unit> {
        return syncManager?.pullNoteFromServer(noteId)
            ?: Result.failure(Exception("Sync manager not initialized"))
    }

    /**
     * Отправить заметку на сервер
     */
    suspend fun pushNoteToServer(noteId: Long): Result<Unit> {
        return syncManager?.pushNoteToServer(noteId)
            ?: Result.failure(Exception("Sync manager not initialized"))
    }

    /**
     * Получить состояние синхронизации
     */
    fun getSyncState(): Flow<org.example.project.data.sync.SyncState>? {
        return syncManager?.syncState
    }

    /**
     * Вставить заметку и сразу синхронизировать с сервером
     */
    suspend fun insertNoteAndSync(note: Note): Long {
        val noteId = insertNote(note)
        syncManager?.pushNoteToServer(noteId)
        return noteId
    }

    /**
     * Обновить заметку и сразу синхронизировать с сервером
     */
    suspend fun updateNoteAndSync(noteId: Long, note: Note) {
        updateNote(noteId, note)
        syncManager?.pushNoteToServer(noteId)
    }

    /**
     * Удалить заметку и сразу синхронизировать с сервером
     */
    suspend fun deleteNoteAndSync(noteId: Long) {
        deleteNote(noteId)
        noteApiService?.deleteNote(noteId)
    }
}

