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

class CheckTranscribingViewModel(
    private val authRepository: AuthRepository,
    private val analyzerRepository: AnalyzerRepository
) : ViewModel() {
    private val _transcription = MutableStateFlow<String>("")
    val transcription = _transcription.asStateFlow()

    var jobId: String = ""
        private set


    fun setTranscription(text: String) {
        _transcription.update { text }
    }

    fun loadJobResult(jobId: String) {
        this@CheckTranscribingViewModel.jobId = jobId

        viewModelScope.launch {
            val response = analyzerRepository.getTranscribingResult(jobId)

            if (response != null) {
                var text = ""

                response.forEach { item ->
                    text += "${item.speaker}: ${item.text}\n"
                }

                _transcription.update { text }
            } else {
                _transcription.update { "ошибка загрузки" }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authRepository = AppDependencies.container.authRepository
                val analyzerRepository = AppDependencies.container.analyzerRepository
                CheckTranscribingViewModel(
                    authRepository = authRepository,
                    analyzerRepository = analyzerRepository
                )
            }
        }
    }
}