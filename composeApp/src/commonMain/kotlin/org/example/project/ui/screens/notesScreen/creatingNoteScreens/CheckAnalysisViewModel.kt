package org.example.project.ui.screens.notesScreen.creatingNoteScreens

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.AppDependencies
import org.example.project.data.analyzer.AnalyzerRepository
import org.example.project.data.auth.AuthRepository
import org.example.project.model.MeetingSummary
import org.example.project.model.TaskItem

data class TaskCell(
    val task: TaskItem,
    val isUsed: Boolean,
    // we can add auxiliary info like id here
)

data class AnalysisResultsUiData(
    val loadingState: LoadingState = LoadingState.LOADING,
    val summary: String = "зарузка"
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

    private val _uiData = MutableStateFlow(AnalysisResultsUiData())
    val uiData = _uiData.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskCell>>(listOf())
    val tasks = _tasks.asStateFlow()


    fun updateTaskUsing(i: Int, isUsed: Boolean) {
        _tasks.update { currentList ->
            currentList.toMutableList().apply {
                this[i] = this[i].copy(isUsed = isUsed)
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
                    currentState.copy(loadingState = LoadingState.SUCCESS)
                }
            } else {
                _uiData.update { currentState ->
                    currentState.copy(loadingState = LoadingState.FAIL)
                }
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