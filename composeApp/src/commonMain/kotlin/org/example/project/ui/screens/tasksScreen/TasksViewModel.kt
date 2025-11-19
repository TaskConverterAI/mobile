package org.example.project.ui.screens.tasksScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.AppDependencies
import org.example.project.data.analyzer.AnalyzerRepository
import org.example.project.data.auth.AuthRepository
import org.example.project.model.AnalysisJob

class TasksViewModel(
    private val authRepository: AuthRepository,
    private val analyzerRepository: AnalyzerRepository
) : ViewModel() {
    private val _currentJobs = MutableStateFlow(listOf<AnalysisJob>())
    val currentJobs = _currentJobs.asStateFlow()

    fun startUpdateJobsList() {
        viewModelScope.launch {
            while (true) {
                val response = analyzerRepository.getAllJobs(authRepository.getUserId())

                if (response != null) {
                    _currentJobs.update { response }
                }

                delay(15000)
            }
        }
    }

    fun closeErrorMsg(job: AnalysisJob) {

        _currentJobs.update { currentState ->
            currentState.toMutableList().apply {
                this.remove(job)
            }
        }

        viewModelScope.launch {
            val response = analyzerRepository.getAnalysisResult(job.jobId)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authRepository = AppDependencies.container.authRepository
                val analyzerRepository = AppDependencies.container.analyzerRepository
                TasksViewModel(
                    authRepository = authRepository,
                    analyzerRepository = analyzerRepository
                )
            }
        }
    }
}