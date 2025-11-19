package org.example.project.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.example.project.data.database.entities.CommentEntity
import org.example.project.data.database.entities.NoteEntity
import org.example.project.data.database.entities.NoteWithGroup
import org.example.project.data.database.entities.NoteWithTasks
import org.example.project.data.database.relations.NoteWithComments

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY creationDate DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): NoteEntity?

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteWithComments(noteId: Long): NoteWithComments?

    @Transaction
    @Query("SELECT * FROM notes ORDER BY creationDate DESC")
    fun getAllNotesWithComments(): Flow<List<NoteWithComments>>

    // Получение заметки с группой
    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteWithGroup(noteId: Long): NoteWithGroup?

    @Transaction
    @Query("SELECT * FROM notes ORDER BY creationDate DESC")
    fun getAllNotesWithGroup(): Flow<List<NoteWithGroup>>

    // Получение заметки со всеми связанными задачами и группой
    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteWithTasks(noteId: Long): NoteWithTasks?

    @Transaction
    @Query("SELECT * FROM notes ORDER BY creationDate DESC")
    fun getAllNotesWithTasks(): Flow<List<NoteWithTasks>>

    // Поиск по группе (используем groupId вместо group)
    @Query("SELECT * FROM notes WHERE groupId = :groupId ORDER BY creationDate DESC")
    fun getNotesByGroup(groupId: String): Flow<List<NoteEntity>>

    @Transaction
    @Query("SELECT * FROM notes WHERE groupId = :groupId ORDER BY creationDate DESC")
    fun getNotesByGroupWithTasks(groupId: String): Flow<List<NoteWithTasks>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Query("SELECT * FROM comments WHERE noteId = :noteId ORDER BY timestamp DESC")
    suspend fun getCommentsForNote(noteId: Long): List<CommentEntity>
}

