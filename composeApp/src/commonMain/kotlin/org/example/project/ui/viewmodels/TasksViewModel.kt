package org.example.project.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.example.project.AppDependencies
import org.example.project.data.auth.AuthRepository
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Priority
import org.example.project.data.commonData.Status
import org.example.project.data.commonData.Task
import org.example.project.data.database.DatabaseProvider
import org.example.project.data.database.repository.GroupRepository
import org.example.project.data.database.repository.TaskRepository
import kotlin.collections.plus

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Загрузить все задачи с полными деталями (группа, исполнитель, заметка)
     */
    fun loadTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = authRepository.getUserIdByToken()
                val response = taskRepository.getAllTasks(userId)
                if (response != null) {
                    _tasks.value = response.filter { note -> note.groupId ==  null  }
                }

                val groups = groupRepository.getAllGroups(userId = userId)
                groups?.forEach { group ->
                    val groupNotes = taskRepository.getTasksByGroup(group.id)
                    if (groupNotes != null) {
                        _tasks.value = _tasks.value.plus(groupNotes)
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Добавить новую задачу
     * @param task - задача с объектами Group, User, Note (автоматически преобразуются в ID)
     */
    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                val userId = authRepository.getUserIdByToken()
                taskRepository.insertTask(userId, task)
                // Задачи обновятся автоматически через Flow
            } catch (e: Exception) {
                _error.value = e.message
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
                taskRepository.updateTask(taskId, task)
            } catch (e: Exception) {
                _error.value = e.message
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
            } catch (e: Exception) {
                _error.value = e.message
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
                taskRepository.addCommentToTask(taskId, comment)
            } catch (e: Exception) {
                _error.value = e.message
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
            taskRepository.getTaskById(taskId)
        } catch (e: Exception) {
            _error.value = e.message
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
                    AppDependencies.container.groupRepository
                )
            }
        }
    }
}
