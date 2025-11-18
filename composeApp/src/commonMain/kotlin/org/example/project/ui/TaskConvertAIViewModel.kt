package org.example.project.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TaskConvertAIViewModel(): ViewModel() {
    var showOverview: Boolean by mutableStateOf(true)
        private set

    private val _selectedFileUri = MutableStateFlow<String?>(null)
    val selectedFileUri: StateFlow<String?> = _selectedFileUri


    fun onFileSelected(uri: String?) {
        _selectedFileUri.value = uri
    }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TaskConvertAIViewModel()
            }
        }
    }
}


