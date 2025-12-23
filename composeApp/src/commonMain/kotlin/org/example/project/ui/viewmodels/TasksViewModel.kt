package org.example.project.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.AppDependencies
import org.example.project.data.auth.AuthRepository
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Task
import org.example.project.data.database.repository.GroupRepository
import org.example.project.data.database.repository.TaskRepository
import org.example.project.data.notifications.NotificationService
import org.example.project.ui.screens.statusToast.StatusType

data class TaskToastMessage(
    val message: String,
    val type: StatusType
)

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository,
    private val notificationService: NotificationService
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())

    private val _groups = MutableStateFlow<List<org.example.project.data.commonData.Group>>(emptyList())
    val groups: StateFlow<List<org.example.project.data.commonData.Group>> = _groups.asStateFlow()

    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    val selectedGroupId: StateFlow<Long?> = _selectedGroupId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _toastMessage = MutableStateFlow<TaskToastMessage?>(null)
    val toastMessage: StateFlow<TaskToastMessage?> = _toastMessage.asStateFlow()

    /**
     * Загрузить все задачи с полными деталями (группа, исполнитель, заметка)
     */
    fun loadTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                println("TasksViewModel: Начало загрузки задач")
                val userId = authRepository.getUserIdByToken()
                println("TasksViewModel: userId = $userId")

                val response = taskRepository.getAllTasks(userId)
                println("TasksViewModel: получено ${response?.size ?: 0} задач")

                val allLoadedTasks = mutableListOf<Task>()

                if (response != null) {
                    allLoadedTasks.addAll(response.filter { task -> task.groupId == null })
                    println("TasksViewModel: добавлено ${allLoadedTasks.size} задач без группы")
                } else {
                    _error.value = "Не удалось загрузить задачи"
                    _toastMessage.value = TaskToastMessage(
                        message = "Не удалось загрузить задачи",
                        type = StatusType.ERROR
                    )
                }

                val groups = groupRepository.getAllGroups(userId = userId)
                println("TasksViewModel: получено ${groups?.size ?: 0} групп")

                if (groups != null) {
                    _groups.value = groups
                    println("TasksViewModel: группы установлены в StateFlow")
                }

                groups?.forEach { group ->
                    val groupTasks = taskRepository.getTasksByGroup(group.id)
                    if (groupTasks != null) {
                        allLoadedTasks.addAll(groupTasks)
                        println("TasksViewModel: добавлено ${groupTasks.size} задач из группы ${group.name}")
                    } else {
                        _error.value = "Не удалось загрузить задачи группы ${group.name}"
                        _toastMessage.value = TaskToastMessage(
                            message = "Не удалось загрузить задачи группы ${group.name}",
                            type = StatusType.ERROR
                        )
                    }
                }

                _allTasks.value = allLoadedTasks
                applyFilter()

                println("TasksViewModel: Итого загружено ${_allTasks.value.size} задач")

//                _toastMessage.value = TaskToastMessage(
//                    message = "Задачи загружены",
//                    type = StatusType.SUCCESS
//                )
            } catch (e: Exception) {
                _error.value = e.message
                _toastMessage.value = TaskToastMessage(
                    message = "Ошибка загрузки задач: ${e.message}",
                    type = StatusType.ERROR
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Применить фильтр по группе
     */
    private fun applyFilter() {
        _tasks.value = if (_selectedGroupId.value == null) {
            _allTasks.value
        } else {
            _allTasks.value.filter { it.groupId == _selectedGroupId.value }
        }
        println("TasksViewModel: Фильтр применен: ${_tasks.value.size} задач (selectedGroupId = ${_selectedGroupId.value})")
    }

    /**
     * Выбрать фильтр по группе
     */
    fun selectGroup(groupId: Long?) {
        println("TasksViewModel: selectGroup вызвана с groupId = $groupId")
        _selectedGroupId.value = groupId
        applyFilter()
    }

    /**
     * Добавить новую задачу
     * @param task - задача с объектами Group, User, Note (автоматически преобразуются в ID)
     */
    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                val userId = authRepository.getUserIdByToken()
                val result = taskRepository.insertTask(userId, task)
                if (result == null) {
                    _error.value = "Не удалось создать задачу"
                    _toastMessage.value = TaskToastMessage(
                        message = "Не удалось создать задачу",
                        type = StatusType.ERROR
                    )
                } else {
                    // Планируем уведомления для новой задачи
                    try {
                        notificationService.scheduleTaskNotifications(result)
                        println("TasksViewModel: Уведомления запланированы для задачи ${result.id}")
                    } catch (e: Exception) {
                        println("TasksViewModel: Ошибка при планировании уведомлений: ${e.message}")
                        // Не показываем ошибку пользователю, так как задача создана успешно
                    }
                }

//                _toastMessage.value = TaskToastMessage(
//                    message = "Задача успешно добавлена",
//                    type = StatusType.SUCCESS
//                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка при создании задачи"
                _toastMessage.value = TaskToastMessage(
                    message = "Ошибка при создании задачи: ${e.message}",
                    type = StatusType.ERROR
                )
            }
        }
    }

    /**
     * Обновить существующую задачу
     * @param taskId - ID задачи для обновления
     * @param task - обновлённые данные задачи
     */
    fun updateTask(taskId: Long, task: Task) {
        viewModelScope.launch {
            try {
                val result = taskRepository.updateTask(taskId, task)
                if (result == null) {
                    _error.value = "Не удалось обновить задачу"
                    _toastMessage.value = TaskToastMessage(
                        message = "Не удалось обновить задачу",
                        type = StatusType.ERROR
                    )
                } else {
                    // Отменяем старые уведомления и планируем новые
                    try {
                        notificationService.cancelTaskNotifications(taskId)
                        notificationService.scheduleTaskNotifications(result)
                        println("TasksViewModel: Уведомления обновлены для задачи ${result.id}")
                    } catch (e: Exception) {
                        println("TasksViewModel: Ошибка при обновлении уведомлений: ${e.message}")
                    }
                }

//                _toastMessage.value = TaskToastMessage(
//                    message = "Задача успешно обновлена",
//                    type = StatusType.SUCCESS
//                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка при обновлении задачи"
                _toastMessage.value = TaskToastMessage(
                    message = "Ошибка при обновлении задачи: ${e.message}",
                    type = StatusType.ERROR
                )
            }
        }
    }

    /**
     * Удалить задачу
     * @param taskId - ID задачи для удаления
     */
    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(taskId)

                // Отменяем все уведомления для удаленной задачи
                try {
                    notificationService.cancelTaskNotifications(taskId)
                    println("TasksViewModel: Уведомления отменены для удаленной задачи $taskId")
                } catch (e: Exception) {
                    println("TasksViewModel: Ошибка при отмене уведомлений: ${e.message}")
                }

//                _toastMessage.value = TaskToastMessage(
//                    message = "Задача успешно удалена",
//                    type = StatusType.SUCCESS
//                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка при удалении задачи"
                _toastMessage.value = TaskToastMessage(
                    message = "Ошибка при удалении задачи: ${e.message}",
                    type = StatusType.ERROR
                )
            }
        }
    }

    /**
     * Добавить комментарий к задаче
     * @param taskId - ID задачи
     * @param comment - комментарий для добавления
     */
    fun addCommentToTask(taskId: Long, comment: Comment) {
        viewModelScope.launch {
            try {
                val res = taskRepository.addCommentToTask(taskId, comment)
                if (res == null) {
                    _error.value = "Не удалось добавить комментарий"
                    _toastMessage.value = TaskToastMessage(
                        message = "Не удалось добавить комментарий",
                        type = StatusType.ERROR
                    )
                }
//                else {
//                    _toastMessage.value = TaskToastMessage(
//                        message = "Комментарий добавлен",
//                        type = StatusType.SUCCESS
//                    )
//                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка при добавлении комментария"
                _toastMessage.value = TaskToastMessage(
                    message = "Ошибка при добавлении комментария: ${e.message}",
                    type = StatusType.ERROR
                )
            }
        }
    }

    /**
     * Получить задачу по ID
     * @param taskId - ID задачи
     * @return Task с полными деталями (группа, исполнитель, заметка, комментарии)
     */
    suspend fun getTaskById(taskId: Long): Task? {
        return try {
            val res: Task? = taskRepository.getTaskById(taskId)

            if (res == null) {
                _error.value = "Не удалось загрузить задачу"
                _toastMessage.value = TaskToastMessage(
                    message = "Не удалось загрузить задачу",
                    type = StatusType.ERROR
                )
            }

//            _toastMessage.value = TaskToastMessage(
//                message = "Задача загружена",
//                type = StatusType.SUCCESS
//            )

            res
        } catch (e: Exception) {
            _error.value = e.message
            _toastMessage.value = TaskToastMessage(
                message = "Ошибка получения задачи: ${e.message}",
                type = StatusType.ERROR
            )
            null
        }
    }

//    /**
//     * Загрузить задачи конкретной группы
//     * @param groupId - ID группы
//     */
//    fun getTasksByGroup(groupId: Long) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            taskRepository.getTasksByGroup(groupId)
//                .catch { e ->
//                    _error.value = e.message
//                    _isLoading.value = false
//                }
//                .collect { tasksList ->
//                    _tasks.value = tasksList
//                    _isLoading.value = false
//                }
//        }
//    }

//    /**
//     * Загрузить задачи конкретного исполнителя
//     * @param assigneeId - ID пользователя (исполнителя)
//     */
//    fun getTasksByAssignee(assigneeId: Long) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            taskRepository.getTasksByAssignee(assigneeId)
//                .catch { e ->
//                    _error.value = e.message
//                    _isLoading.value = false
//                }
//                .collect { tasksList ->
//                    _tasks.value = tasksList
//                    _isLoading.value = false
//                }
//        }
//    }

//    /**
//     * Загрузить задачи конкретной заметки
//     * @param noteId - ID заметки
//     */
//    fun getTasksByNote(noteId: Long) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            taskRepository.getTasksByNote(noteId)
//                .catch { e ->
//                    _error.value = e.message
//                    _isLoading.value = false
//                }
//                .collect { tasksList ->
//                    _tasks.value = tasksList
//                    _isLoading.value = false
//                }
//        }
//    }

//    /**
//     * Фильтр по статусу
//     * @param status - статус задачи (TODO, IN_PROGRESS, DONE)
//     */
//    fun getTasksByStatus(status: Status) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            taskRepository.getTasksByStatus(status)
//                .catch { e ->
//                    _error.value = e.message
//                    _isLoading.value = false
//                }
//                .collect { tasksList ->
//                    _tasks.value = tasksList
//                    _isLoading.value = false
//                }
//        }
//    }

//    /**
//     * Фильтр по приоритету
//     * @param priority - приоритет задачи (HIGH, MEDIUM, LOW)
//     */
//    fun getTasksByPriority(priority: Priority) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            taskRepository.getTasksByPriority(priority)
//                .catch { e ->
//                    _error.value = e.message
//                    _isLoading.value = false
//                }
//                .collect { tasksList ->
//                    _tasks.value = tasksList
//                    _isLoading.value = false
//                }
//        }
//    }

    /**
     * Очистить ошибку
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Очистить toast сообщение
     */
    fun clearToast() {
        _toastMessage.value = null
    }

    /**
     * Добавить тестовые данные (для разработки)
     */
    fun insertSampleData() {
        viewModelScope.launch {
            try {
//                taskRepository.insertSampleData()
                println("TasksViewModel: Sample data inserted successfully")
            } catch (e: Exception) {
                _error.value = "Ошибка при добавлении тестовых данных: ${e.message}"
                println("TasksViewModel: Error inserting sample data: ${e.message}")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TasksViewModel(
                    AppDependencies.container.taskRepository,
                    AppDependencies.container.authRepository,
                    AppDependencies.container.groupRepository,
                    AppDependencies.container.notificationService
                )
            }
        }
    }
}
