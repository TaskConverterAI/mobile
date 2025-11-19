package org.example.project.data.database.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Priority
import org.example.project.data.commonData.Status
import org.example.project.data.commonData.Task
import org.example.project.data.database.AppDatabase
import org.example.project.data.database.mappers.toComment
import org.example.project.data.database.mappers.toEntity
import org.example.project.data.database.mappers.toTask
import org.example.project.data.database.mappers.toTaskCommentEntity

class TaskRepository(private val database: AppDatabase) {

    private val taskDao = database.taskDao()
    private val groupDao = database.groupDao()
    private val userDao = database.userDao()
    private val noteDao = database.noteDao()

    /**
     * Получить все задачи с полными деталями (группа, исполнитель, заметка)
     */
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasksWithDetails().map { tasksWithDetails ->
            tasksWithDetails.map { taskWithDetails ->
                // Получаем комментарии для каждой задачи
                val comments = taskDao.getCommentsForTask(taskWithDetails.task.id)
                    .map { it.toComment() }
                taskWithDetails.toTask(comments)
            }
        }
    }

    /**
     * Получить задачу по ID с полными деталями
     */
    suspend fun getTaskById(taskId: String): Task? {
        val taskWithDetails = taskDao.getTaskWithDetails(taskId) ?: return null
        val comments = taskDao.getCommentsForTask(taskId).map { it.toComment() }
        return taskWithDetails.toTask(comments)
    }

    /**
     * Получить задачи по группе
     */
    fun getTasksByGroup(groupId: String): Flow<List<Task>> {
        return taskDao.getTasksByGroupWithDetails(groupId).map { tasksWithDetails ->
            tasksWithDetails.map { taskWithDetails ->
                val comments = taskDao.getCommentsForTask(taskWithDetails.task.id)
                    .map { it.toComment() }
                taskWithDetails.toTask(comments)
            }
        }
    }

    /**
     * Получить задачи по исполнителю
     */
    fun getTasksByAssignee(assigneeId: String): Flow<List<Task>> {
        return taskDao.getTasksByAssigneeWithDetails(assigneeId).map { tasksWithDetails ->
            tasksWithDetails.map { taskWithDetails ->
                val comments = taskDao.getCommentsForTask(taskWithDetails.task.id)
                    .map { it.toComment() }
                taskWithDetails.toTask(comments)
            }
        }
    }

    /**
     * Получить задачи по заметке
     */
    fun getTasksByNote(noteId: Long): Flow<List<Task>> {
        return taskDao.getTasksByNoteWithDetails(noteId).map { tasksWithDetails ->
            tasksWithDetails.map { taskWithDetails ->
                val comments = taskDao.getCommentsForTask(taskWithDetails.task.id)
                    .map { it.toComment() }
                taskWithDetails.toTask(comments)
            }
        }
    }

    /**
     * Получить задачи по статусу
     */
    fun getTasksByStatus(status: Status): Flow<List<Task>> {
        return taskDao.getTasksByStatus(status).map { tasks ->
            tasks.map { task ->
                // Получаем полные детали для каждой задачи
                val taskWithDetails = taskDao.getTaskWithDetails(task.id)
                val comments = taskDao.getCommentsForTask(task.id).map { it.toComment() }
                taskWithDetails?.toTask(comments) ?: throw IllegalStateException("Task not found")
            }
        }
    }

    /**
     * Получить задачи по приоритету
     */
    fun getTasksByPriority(priority: Priority): Flow<List<Task>> {
        return taskDao.getTasksByPriority(priority).map { tasks ->
            tasks.map { task ->
                val taskWithDetails = taskDao.getTaskWithDetails(task.id)
                val comments = taskDao.getCommentsForTask(task.id).map { it.toComment() }
                taskWithDetails?.toTask(comments) ?: throw IllegalStateException("Task not found")
            }
        }
    }

    /**
     * Вставить новую задачу
     * @param task - задача с группой, исполнителем (объекты будут преобразованы в ID)
     * @param noteId - опциональный ID заметки, к которой относится задача
     */
    suspend fun insertTask(task: Task, noteId: Long? = null) {
        // Найти или создать группу
        val groupId = findOrCreateGroup(task.group)

        // Найти или создать пользователя (исполнителя)
        val assigneeId = findOrCreateUser(task.assignee)

        // Вставить задачу с ID
        val taskEntity = task.toEntity(
            groupId = groupId,
            assigneeId = assigneeId,
            noteId = noteId
        )
        taskDao.insertTask(taskEntity)

        // Вставить комментарии
        task.comments.forEach { comment ->
            taskDao.insertComment(comment.toTaskCommentEntity(taskEntity.id))
        }
    }

    /**
     * Обновить задачу
     */
    suspend fun updateTask(taskId: String, task: Task) {
        // Найти или создать группу
        val groupId = findOrCreateGroup(task.group)

        // Найти или создать пользователя
        val assigneeId = findOrCreateUser(task.assignee)

        // Получить текущую задачу для сохранения noteId
        val currentTask = taskDao.getTaskById(taskId)

        // Обновить задачу с новыми ID
        val taskEntity = task.toEntity(
            id = taskId,
            groupId = groupId,
            assigneeId = assigneeId,
            noteId = currentTask?.noteId
        )
        taskDao.updateTask(taskEntity)
    }

    /**
     * Удалить задачу
     */
    suspend fun deleteTask(taskId: String) {
        taskDao.deleteTaskById(taskId)
    }

    /**
     * Добавить комментарий к задаче
     */
    suspend fun addCommentToTask(taskId: String, comment: Comment) {
        taskDao.insertComment(comment.toTaskCommentEntity(taskId))
    }

    /**
     * Получить комментарии для задачи
     */
    suspend fun getCommentsForTask(taskId: String): List<Comment> {
        return taskDao.getCommentsForTask(taskId).map { it.toComment() }
    }

    /**
     * Найти группу по имени или создать новую
     * @return ID группы
     */
    private suspend fun findOrCreateGroup(group: org.example.project.data.commonData.Group): String? {
        // Если группа дефолтная
        val groupIdStr: String = group.id
        if (group.name == "Без группы" || groupIdStr.isEmpty() && group.name.isEmpty()) {
            return null
        }

        // Если у группы уже есть ID, используем его
        if (groupIdStr.isNotEmpty()) {
            return groupIdStr
        }

        // Ищем группу по имени
        val allGroups = groupDao.getAllGroups()
        var groupId: String? = null

        allGroups.collect { groups ->
            groupId = groups.find { it.name == group.name }?.id
        }

        // Если группа не найдена, создаём новую
        if (groupId == null) {
            val newGroup = group.toEntity()
            groupDao.insert(newGroup)
            groupId = newGroup.id
        }

        return groupId
    }

    /**
     * Найти пользователя по email или создать нового
     * @return ID пользователя
     */
    private suspend fun findOrCreateUser(user: org.example.project.data.commonData.User): String? {
        // Если пользователь дефолтный
        val userIdStr: String = user.id
        if (user.email == "Не назначен" || userIdStr.isEmpty() && user.email.isEmpty()) {
            return null
        }

        // Если у пользователя уже есть ID, используем его
        if (userIdStr.isNotEmpty()) {
            return userIdStr
        }

        // Ищем пользователя по email
        val existingUser = userDao.getByEmail(user.email)

        // Если пользователь не найден, создаём нового
        if (existingUser != null) {
            return existingUser.id
        } else {
            val newUser = user.toEntity()
            userDao.insert(newUser)
            return newUser.id
        }
    }
}

