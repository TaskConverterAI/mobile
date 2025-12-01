package org.example.project.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.example.project.data.commonData.Priority
import org.example.project.data.commonData.Status
import org.example.project.data.database.entities.CommentEntity
import org.example.project.data.database.entities.TaskEntity
import org.example.project.data.database.entities.TaskWithDetails
import org.example.project.data.database.relations.TaskWithComments

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskWithComments(taskId: Long): TaskWithComments?

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasksWithComments(): Flow<List<TaskWithComments>>

    // Получение задачи с деталями (группа + исполнитель)
    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskWithDetails(taskId: Long): TaskWithDetails?

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasksWithDetails(): Flow<List<TaskWithDetails>>

    // Поиск по группе (используем groupId вместо group)
    @Query("SELECT * FROM tasks WHERE groupId = :groupId ORDER BY dueDate ASC")
    fun getTasksByGroup(groupId: Long): Flow<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE groupId = :groupId ORDER BY dueDate ASC")
    fun getTasksByGroupWithDetails(groupId: Long): Flow<List<TaskWithDetails>>

    // Поиск по исполнителю
    @Query("SELECT * FROM tasks WHERE assigneeId = :assigneeId ORDER BY dueDate ASC")
    fun getTasksByAssignee(assigneeId: Long): Flow<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE assigneeId = :assigneeId ORDER BY dueDate ASC")
    fun getTasksByAssigneeWithDetails(assigneeId: Long): Flow<List<TaskWithDetails>>

    // Поиск по заметке
    @Query("SELECT * FROM tasks WHERE noteId = :noteId ORDER BY dueDate ASC")
    fun getTasksByNote(noteId: Long): Flow<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE noteId = :noteId ORDER BY dueDate ASC")
    fun getTasksByNoteWithDetails(noteId: Long): Flow<List<TaskWithDetails>>

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY dueDate ASC")
    fun getTasksByStatus(status: Status): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY dueDate ASC")
    fun getTasksByPriority(priority: Priority): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Query("SELECT * FROM comments WHERE taskId = :taskId ORDER BY timestamp DESC")
    suspend fun getCommentsForTask(taskId: Long): List<CommentEntity>
}

