package org.example.project.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.project.AppDependencies
import org.example.project.data.analyzer.AnalyzerRepository
import org.example.project.data.auth.AuthRepository
import org.example.project.data.auth.UserAuthPreferencesRepository

class TaskConvertAIViewModel(
    private val authPreferencesRepository: UserAuthPreferencesRepository,
    private val authRepository: AuthRepository,
    private val analyzerRepository: AnalyzerRepository
) : ViewModel() {
    var showOverview: Boolean by mutableStateOf(true)
        private set
    var mustLogIn: Boolean by mutableStateOf(true)
        private set

    init {
        runBlocking {
            showOverview = authPreferencesRepository.showTutorial.first()
            mustLogIn = !authRepository.refresh()
        }
    }

    private val _selectedFileUri = MutableStateFlow<String?>(null)
    val selectedFileUri: StateFlow<String?> = _selectedFileUri

    fun hideOverview() {
        viewModelScope.launch {
            authPreferencesRepository.saveShowTutorial(false)
        }
    }
    fun onFileSelected(uri: String?) {
        if (uri == null)
            return
        _selectedFileUri.value = uri
        viewModelScope.launch {
            analyzerRepository.transcribeAudio(authRepository.getUserId(), uri)
        }
    }

    fun clearFile() {
        _selectedFileUri.value = null
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authPreferencesRepository = AppDependencies.container.userAuthPreferencesRepository
                val authRepository = AppDependencies.container.authRepository
                val analyzerRepository = AppDependencies.container.analyzerRepository
                TaskConvertAIViewModel(
                    authPreferencesRepository = authPreferencesRepository,
                    authRepository = authRepository,
                    analyzerRepository = analyzerRepository
                )
            }
        }
    }
}


