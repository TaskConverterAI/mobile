package org.example.project.data.database.repository

import co.touchlab.kermit.Logger
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Task
import org.example.project.data.commonData.toAddCommentRequest
import org.example.project.data.commonData.toComment
import org.example.project.data.commonData.toCreateRequest
import org.example.project.data.commonData.toTask
import org.example.project.data.commonData.toUpdateRequest
import org.example.project.data.database.AppDatabase
import org.example.project.data.network.NoteApiService
import kotlin.time.ExperimentalTime

class TaskRepository(
    private val database: AppDatabase,
    private val taskApiService: NoteApiService? = null) {

    private val taskDao = database.taskDao()
    private val groupDao = database.groupDao()
    private val userDao = database.userDao()
    private val noteDao = database.noteDao()

    suspend fun getGroupTaskCount(groupId: Long) : Int {
        val result = taskApiService?.getAllGroupTask(groupId = groupId)
        val retVal = result?.fold(
            onSuccess = {response -> response.size},
            onFailure = {response -> 0}
        )
        if (retVal != null){
            return retVal
        }

        return 0
    }

    /**
     * Получить все задачи с полными деталями (группа, исполнитель, заметка)
     */
    @OptIn(ExperimentalTime::class)
    suspend fun getAllTasks(userId: Long): List<Task>? {
        val result =  taskApiService?.getAllTasks(userId)
        val retVal = result?.fold(
            onSuccess = { response ->
                response.map { taskDto -> taskDto.toTask() }
            },
            onFailure = { error ->
                error.printStackTrace()
                null
            }
        )
//        return taskDao.getAllTasksWithDetails().map { tasksWithDetails ->
//            tasksWithDetails.map { taskWithDetails ->
//                val comments = taskDao.getCommentsForTask(taskWithDetails.task.id)
//                    .map { it.toComment() }
//                taskWithDetails.toTask(comments)
//            }
//        }
        return  retVal
    }

    /**
     * Получить задачу по ID с полными деталями
     */
    @OptIn(ExperimentalTime::class)
    suspend fun getTaskById(taskId: Long): Task? {
        val result =  taskApiService?.getTaskDetails(taskId)
        val retVal = result?.fold(
            onSuccess = { taskDto ->
                taskDto.toTask()

            },
            onFailure = { error ->
                error.printStackTrace()
                null
            }
        )
//        val taskWithDetails = taskDao.getTaskWithDetails(taskId) ?: return null
//        val comments = taskDao.getCommentsForTask(taskId).map { it.toComment() }
//        return taskWithDetails.toTask(comments)
        return retVal
    }

    /**
     * Получить задачи по группе
     */
    @OptIn(ExperimentalTime::class)
    suspend fun getTasksByGroup(groupId: Long): List<Task>? {
        val result =  taskApiService?.getAllGroupTask(groupId)
        val retVal = result?.fold(
            onSuccess = { response ->
                response.map { taskDto -> taskDto.toTask() }
            },
            onFailure = { error ->
                error.printStackTrace()
                null
            }
        )
//        return taskDao.getTasksByGroupWithDetails(groupId).map { tasksWithDetails ->
//            tasksWithDetails.map { taskWithDetails ->
//                val comments = taskDao.getCommentsForTask(taskWithDetails.task.id)
//                    .map { it.toComment() }
//                taskWithDetails.toTask(comments)
//            }
//        }
        return retVal
    }

    /**
     * Получить задачи по исполнителю
     */
//    fun getTasksByAssignee(assigneeId: Long): Flow<List<Task>> {
//        return taskDao.getTasksByAssigneeWithDetails(assigneeId).map { tasksWithDetails ->
//            tasksWithDetails.map { taskWithDetails ->
//                val comments = taskDao.getCommentsForTask(taskWithDetails.task.id)
//                    .map { it.toComment() }
//                taskWithDetails.toTask(comments)
//            }
//        }
//    }

    /**
     * Получить задачи по заметке
     */
//    fun getTasksByNote(noteId: Long): Flow<List<Task>> {
//        return taskDao.getTasksByNoteWithDetails(noteId).map { tasksWithDetails ->
//            tasksWithDetails.map { taskWithDetails ->
//                val comments = taskDao.getCommentsForTask(taskWithDetails.task.id)
//                    .map { it.toComment() }
//                taskWithDetails.toTask(comments)
//            }
//        }
//    }

    /**
     * Получить задачи по статусу
     */
//    fun getTasksByStatus(status: Status): Flow<List<Task>> {
//        return taskDao.getTasksByStatus(status).map { tasks ->
//            tasks.map { task ->
//                // Получаем полные детали для каждой задачи
//                val taskWithDetails = taskDao.getTaskWithDetails(task.id)
//                val comments = taskDao.getCommentsForTask(task.id).map { it.toComment() }
//                taskWithDetails?.toTask(comments) ?: throw IllegalStateException("Task not found")
//            }
//        }
//    }

    /**
     * Получить задачи по приоритету
     */
//    fun getTasksByPriority(priority: Priority): Flow<List<Task>> {
//        return taskDao.getTasksByPriority(priority).map { tasks ->
//            tasks.map { task ->
//                val taskWithDetails = taskDao.getTaskWithDetails(task.id)
//                val comments = taskDao.getCommentsForTask(task.id).map { it.toComment() }
//                taskWithDetails?.toTask(comments) ?: throw IllegalStateException("Task not found")
//            }
//        }
//    }

    /**
     * Вставить новую задачу
     * @param task - задача с группой, исполнителем (объекты будут преобразованы в ID)
     * @param noteId - опциональный ID заметки, к которой относится задача
     */
    @OptIn(ExperimentalTime::class)
    suspend fun insertTask(userId: Long, task: Task) : Task? {

        val result = taskApiService?.createTask(task.toCreateRequest())
        val retVal = result?.fold(
            onSuccess = {response ->
                response.toTask()
            },

            onFailure = {
                    error ->
                error.printStackTrace()
                null
            }
        )
//        val taskEntity = task.toEntity()
//        taskDao.insertTask(taskEntity)
//
//        task.comments.forEach { comment ->
//            taskDao.insertComment(comment.toTaskCommentEntity(taskEntity.id))
//        }
        return retVal
    }

    /**
     * Обновить задачу
     */
    suspend fun updateTask(taskId: Long, task: Task) : Task? {
        val result = taskApiService?.updateTask(taskId, task.toUpdateRequest())
        val retVal = result?.fold(
            onSuccess = {response ->
                response.toTask()
            },

            onFailure = {
                    error ->
                error.printStackTrace()
                null
            }
        )
//        val currentTask = taskDao.getTaskById(taskId)
//
//        val taskEntity = task.toEntity()
//        taskDao.updateTask(taskEntity)
        return retVal
    }

    /**
     * Удалить задачу
     */
    suspend fun deleteTask(taskId: Long) {
        taskApiService?.deleteTask(taskId)
        //taskDao.deleteTaskById(taskId)
    }

    /**
     * Добавить комментарий к задаче
     */
    suspend fun addCommentToTask(taskId: Long, comment: Comment) : Comment? {
        val result = taskApiService?.addCommentToTask(
            taskId,
            comment.toAddCommentRequest())
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
        //taskDao.insertComment(comment.toTaskCommentEntity(taskId))
        return retVal
    }

    suspend fun deleteCommentFromTask(comment: Comment)  {
        taskApiService?.deleteCommentFromTask(comment.id)
    }

    /**
     * Получить комментарии для задачи
     */
//    suspend fun getCommentsForTask(taskId: Long): List<Comment> {
//        return taskDao.getCommentsForTask(taskId).map { it.toComment() }
//    }

//    /**
//     * Найти группу по имени или создать новую
//     * @return ID группы
//     */
//    private suspend fun findOrCreateGroup(group: org.example.project.data.commonData.Group): String? {
//        // Если группа дефолтная
//        val groupIdStr: String = group.id
//        if (group.name == "Без группы" || groupIdStr.isEmpty() && group.name.isEmpty()) {
//            return null
//        }
//
//        // Если у группы уже есть ID, используем его
//        if (groupIdStr.isNotEmpty()) {
//            return groupIdStr
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

//    /**
//     * Найти пользователя по email или создать нового
//     * @return ID пользователя
//     */
//    private suspend fun findOrCreateUser(user: org.example.project.data.commonData.User): String? {
//        // Если пользователь дефолтный
//        val userIdStr: String = user.id
//        if (user.email == "Не назначен" || userIdStr.isEmpty() && user.email.isEmpty()) {
//            return null
//        }
//
//        // Если у пользователя уже есть ID, используем его
//        if (userIdStr.isNotEmpty()) {
//            return userIdStr
//        }
//
//        // Ищем пользователя по email
//        val existingUser = userDao.getByEmail(user.email)
//
//        // Если пользователь не найден, создаём нового
//        if (existingUser != null) {
//            return existingUser.id
//        } else {
//            val newUser = user.toEntity()
//            userDao.insert(newUser)
//            return newUser.id
//        }
//    }

//    /**
//     * Вставить тестовые данные для разработки
//     */
//    @OptIn(kotlin.time.ExperimentalTime::class)
//    suspend fun insertSampleData() {
//        // Создать тестовую группу
//        val testGroup = org.example.project.data.commonData.Group(
//            id = "test-group-1",
//            name = "Рабочая группа",
//            description = "Тестовая группа для разработки",
//            ownerId = "user-1",
//            memberCount = 3,
//            createdAt = "2024-01-01",
//            taskCount = 2
//        )
//        groupDao.insert(testGroup.toEntity())
//
//        // Создать тестовых пользователей
//        val testUser1 = org.example.project.data.commonData.User(
//            id = "user-1",
//            email = "ivan@test.com",
//            username = "Иван",
//            privileges = org.example.project.data.commonData.Privileges.owner
//        )
//        val testUser2 = org.example.project.data.commonData.User(
//            id = "user-2",
//            email = "maria@test.com",
//            username = "Мария",
//            privileges = org.example.project.data.commonData.Privileges.member
//        )
//        userDao.insert(testUser1.toEntity())
//        userDao.insert(testUser2.toEntity())
//
//        // Создать тестовые задачи
//        val task1 = Task(
//            id = "task-1",
//            title = "Разработать UI",
//            description = "Создать интерфейс для экрана задач",
//            comments = listOf(
//                Comment(
//                    author = "Иван",
//                    content = "Начал работу над дизайном",
//                    timestamp = Clock.System.now().toEpochMilliseconds() - 86400000
//                )
//            ),
//            group = testGroup,
//            assignee = testUser1,
//            dueDate = Clock.System.now().toEpochMilliseconds() + 172800000, // +2 дня
//            geotag = "Офис",
//            priority = Priority.HIGH,
//            status = Status.IN_PROGRESS
//        )
//
//        val task2 = Task(
//            id = "task-2",
//            title = "Написать тесты",
//            description = "Добавить unit-тесты для репозиториев",
//            comments = listOf(
//                Comment(
//                    author = "Мария",
//                    content = "Подготовила список тестов",
//                    timestamp = Clock.System.now().toEpochMilliseconds() - 43200000
//                )
//            ),
//            group = testGroup,
//            assignee = testUser2,
//            dueDate = Clock.System.now().toEpochMilliseconds() + 259200000, // +3 дня
//            geotag = "Удалённо",
//            priority = Priority.MEDIUM,
//            status = Status.TODO
//        )
//
//        val task3 = Task(
//            id = "task-3",
//            title = "Провести код-ревью",
//            description = "Проверить код перед релизом",
//            comments = emptyList(),
//            group = testGroup,
//            assignee = testUser1,
//            dueDate = Clock.System.now().toEpochMilliseconds() + 345600000, // +4 дня
//            geotag = "Офис",
//            priority = Priority.LOW,
//            status = Status.TODO
//        )
//
//        // Вставить задачи
//        insertTask(task1)
//        insertTask(task2)
//        insertTask(task3)
//
//        println("TaskRepository: Sample data inserted - 3 tasks, 1 group, 2 users")
//    }
}

