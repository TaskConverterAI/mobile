package org.example.project.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import org.example.project.data.analyzer.AnalyzerRepository
import org.example.project.data.auth.AuthRepository


data class UiData (
    var showOverview: Boolean = true,
    var selectedFileUri: String? = null,
    var startSending: Boolean = false,
    var convertVideoAudioPercent: Int = 0,
    var sendAudioFailed: Boolean = false
)

class TaskConvertAIViewModel(
    private val authRepository: AuthRepository,
    private val analyzerRepository: AnalyzerRepository
) : ViewModel() {
    private val _uiData = MutableStateFlow<UiData>(UiData())
    val uiData: StateFlow<UiData> = _uiData.asStateFlow()


    fun onFileSelected(uri: String?) {
        if (uri == null)
            return
        _uiData.value.startSending = true
        viewModelScope.launch {
            val result = analyzerRepository.transcribeAudio(authRepository.getUserId(), uri) { percents ->
                _uiData.value.convertVideoAudioPercent = (percents * 100).toInt()
            }

            if (result) {
                _uiData.value.selectedFileUri = uri
            } else {
                _uiData.value.sendAudioFailed = true
            }
        }
    }

    fun clearFile() {
        _uiData.value.selectedFileUri = null
        _uiData.value.sendAudioFailed = false
        _uiData.value.convertVideoAudioPercent = 0
        _uiData.value.startSending = false
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authRepository = AppDependencies.container.authRepository
                val analyzerRepository = AppDependencies.container.analyzerRepository
                TaskConvertAIViewModel(
                    authRepository = authRepository,
                    analyzerRepository = analyzerRepository
                )
            }
        }
    }
}


