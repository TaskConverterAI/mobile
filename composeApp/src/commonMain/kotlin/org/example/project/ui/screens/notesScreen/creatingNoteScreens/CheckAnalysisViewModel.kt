package org.example.project.ui.screens.notesScreen.creatingNoteScreens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.AppDependencies
import org.example.project.data.analyzer.AnalyzerRepository
import org.example.project.data.auth.AuthRepository
import org.example.project.data.commonData.Deadline
import org.example.project.data.database.DatabaseProvider
import org.example.project.data.commonData.Note
import org.example.project.data.commonData.Task
import org.example.project.data.commonData.Priority
import org.example.project.data.commonData.Status
import org.example.project.model.TaskItem
import org.example.project.ui.theme.PrimaryBase
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class TaskCell(
    val task: TaskItem,
    val isUsed: Boolean,
    // we can add auxiliary info like id here
)

data class AnalysisResultsUiData(
    val loadingState: LoadingState = LoadingState.LOADING,
    val summary: String = "зарузка",
    val noteTitle: String = "Заметка",
    val noteGeotag: String = "office"
)

enum class LoadingState {
    LOADING,
    SUCCESS,
    FAIL
}

class CheckAnalysisViewModel(
    private val authRepository: AuthRepository,
    private val analyzerRepository: AnalyzerRepository
) : ViewModel() {

    // private val noteRepository = DatabaseProvider.getNoteRepository()
    // private val taskRepository = DatabaseProvider.getTaskRepository()
    // Используем репозитории из AppDependencies.container, где внедрён сетевой сервис
    private val noteRepository = AppDependencies.container.noteRepository
    private val taskRepository = AppDependencies.container.taskRepository

    // Callback для уведомления об обновлении задач
    var onTasksUpdated: (() -> Unit)? = null

    private val _uiData = MutableStateFlow(AnalysisResultsUiData())
    val uiData = _uiData.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskCell>>(listOf())
    val tasks = _tasks.asStateFlow()

    private val _saveInProgress = MutableStateFlow(false)
    val saveInProgress = _saveInProgress.asStateFlow()

    private val maxDescriptionLength = 200


    fun updateTaskUsing(i: Int, isUsed: Boolean) {
        _tasks.update { currentList ->
            currentList.toMutableList().apply {
                this[i] = this[i].copy(isUsed = isUsed)
            }
        }
    }

    fun updateNoteData(title: String, content: String, geotag: String) {
        _uiData.update { currentState ->
            currentState.copy(
                noteTitle = title,
                summary = content,
                noteGeotag = geotag
            )
        }
    }

    fun updateNoteTitle(title: String) {
        _uiData.update { currentState ->
            currentState.copy(noteTitle = title)
        }
    }

    fun updateNoteContent(content: String) {
        val limitedContent = if (content.length > 200) content.take(200) else content
        _uiData.update { currentState ->
            currentState.copy(summary = limitedContent)
        }
    }

    fun updateTaskTitle(index: Int, title: String) {
        _tasks.update { currentList ->
            currentList.toMutableList().apply {
                this[index] = this[index].copy(
                    task = this[index].task.copy(title = title)
                )
            }
        }
    }

    fun updateTaskDescription(index: Int, description: String) {
        _tasks.update { currentList ->
            currentList.toMutableList().apply {
                this[index] = this[index].copy(
                    task = this[index].task.copy(description = description)
                )
            }
        }
    }


    fun loadJobResult(jobId: String) {
        viewModelScope.launch {
            val response = analyzerRepository.getAnalysisResult(jobId)

            if (response != null) {
                _tasks.update {
                    response.tasks.map { task ->
                        TaskCell(task, true)
                    }
                }

                _uiData.update { currentState ->
                    currentState.copy(
                        loadingState = LoadingState.SUCCESS,
                        summary = response.summary
                    )
                }
            } else {
                _uiData.update { currentState ->
                    currentState.copy(loadingState = LoadingState.FAIL)
                }
            }
        }
    }

    /**
     * Сохраняет заметку и выбранные задачи в базу данных через TaskRepository
     * @param onSuccess - callback при успешном сохранении
     * @param onError - callback при ошибке с сообщением об ошибке
     */
    @OptIn(ExperimentalTime::class)
    fun saveNoteAndTasks(
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                _saveInProgress.value = true

                val currentUiData = _uiData.value
                val currentTasks = _tasks.value

                val userId = authRepository.getUserIdByToken()

                val truncatedContent = if (currentUiData.summary.length > maxDescriptionLength) {
                    currentUiData.summary.substring(0, maxDescriptionLength)
                } else {
                    currentUiData.summary
                }

                log("Saving note with truncated content: $truncatedContent")

                val note = Note(
                    title = currentUiData.noteTitle,
                    content = truncatedContent,
                    authorId = userId,
                    geotag = null,
                    groupId = null,
                    comments = emptyList(),
                    color = PrimaryBase
                )

                log("Inserting note for userId: $userId")
                val insertedNote = noteRepository.insertNote(userId, note)
                log("Note insert response: $insertedNote")

                val enabledTasks = currentTasks.filter { it.isUsed }
                log("Total tasks to save: ${enabledTasks.size}")

                enabledTasks.forEachIndexed { index, taskCell ->
                    val task = Task(
                        id = 0L,
                        title = taskCell.task.title,
                        description = taskCell.task.description,
                        comments = emptyList(),
                        authorId = userId,
                        groupId = null,
                        assignee = userId,
                        dueDate = Deadline(
                            Clock.System.now().toEpochMilliseconds(),
                            false
                        ),
                        geotag = null,
                        priority = Priority.MIDDLE,
                        status = Status.UNDONE
                    )

                    log("Attempting to save task ${index + 1}/${enabledTasks.size}: title='${task.title}', description='${task.description}'")
                    log("Task details - authorId: $userId, assignee: $userId, priority: ${task.priority}, status: ${task.status}")

                    val result = taskRepository.insertTask(userId, task)

                    if (result != null) {
                        log("✅ Task ${index + 1} saved successfully! Server response: $result")
                        log("   - Task ID from server: ${result.id}")
                        log("   - Task title from server: ${result.title}")
                    } else {
                        log("❌ Task ${index + 1} FAILED to save! Server returned NULL")
                        log("   - Original task title: ${task.title}")
                        log("   - Original task description: ${task.description}")
                    }
                }

                _saveInProgress.value = false
                log("All tasks and note saved successfully. Triggering tasks update...")
                onTasksUpdated?.invoke() // Уведомляем о необходимости обновить список задач
                log("Tasks update callback invoked")
                onSuccess()
            } catch (e: Exception) {
                _saveInProgress.value = false
                log("Error saving note and tasks: ${e.message}")
                onError(e.message ?: "Ошибка при сохранении")
            }
        }
    }

    private fun log(message: String) {
        println("[CheckAnalysisViewModel] $message")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authRepository = AppDependencies.container.authRepository
                val analyzerRepository = AppDependencies.container.analyzerRepository
                CheckAnalysisViewModel(
                    authRepository = authRepository,
                    analyzerRepository = analyzerRepository
                )
            }
        }
    }
}