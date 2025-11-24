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
import org.example.project.data.database.DatabaseProvider
import org.example.project.data.commonData.Note
import org.example.project.data.commonData.Task
import org.example.project.data.commonData.Group
import org.example.project.data.commonData.User
import org.example.project.data.commonData.Priority
import org.example.project.data.commonData.Status
import org.example.project.data.commonData.Privileges
import org.example.project.model.TaskItem
import org.example.project.model.MeetingSummary
import org.example.project.ui.theme.PrimaryBase

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

    private val noteRepository = DatabaseProvider.getNoteRepository()
    private val taskRepository = DatabaseProvider.getTaskRepository()

    private val _uiData = MutableStateFlow(AnalysisResultsUiData())
    val uiData = _uiData.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskCell>>(listOf())
    val tasks = _tasks.asStateFlow()

    private val _saveInProgress = MutableStateFlow(false)
    val saveInProgress = _saveInProgress.asStateFlow()


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
        _uiData.update { currentState ->
            currentState.copy(summary = content)
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
                _tasks.update { response.tasks.map { task ->
                    TaskCell(task, true)
                } }

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
     * Сохраняет заметку и выбранные задачи в базу данных
     * @param onSuccess - callback при успешном сохранении
     * @param onError - callback при ошибке с сообщением об ошибке
     */
    fun saveNoteAndTasks(
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                _saveInProgress.value = true

                val currentUiData = _uiData.value
                val currentTasks = _tasks.value

                // Создаем группу по умолчанию (можно заменить на выбор пользователя)
                val defaultGroup = Group(
                    id = "",
                    name = "",
                    description = "",
                    ownerId = "",
                    memberCount = 0,
                    createdAt = ""
                )

                // Создаем заметку
                val note = Note(
                    title = currentUiData.noteTitle,
                    content = currentUiData.summary,
                    geotag = currentUiData.noteGeotag,
                    group = defaultGroup,
                    comments = emptyList(),
                    color = PrimaryBase
                )

                // Сохраняем заметку
                noteRepository.insertNote(note)

                // Фильтруем и сохраняем только выбранные задачи
                currentTasks.filter { it.isUsed }.forEach { taskCell ->
                    val defaultUser = User(
                        id = "",
                        email = "",
                        username = "",
                        privileges = Privileges.member
                    )

                    val task = Task(
                        title = taskCell.task.title,
                        description = taskCell.task.description,
                        comments = emptyList(),
                        group = defaultGroup,
                        assignee = defaultUser,
                        dueDate = 0,
                        geotag = currentUiData.noteGeotag,
                        priority = Priority.MEDIUM,
                        status = Status.TODO
                    )

                    taskRepository.insertTask(task)
                }

                _saveInProgress.value = false
                onSuccess()
            } catch (e: Exception) {
                _saveInProgress.value = false
                onError(e.message ?: "Ошибка при сохранении")
            }
        }
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