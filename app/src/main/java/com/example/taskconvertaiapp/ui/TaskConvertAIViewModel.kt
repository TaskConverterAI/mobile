package com.example.taskconvertaiapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory


class TaskConvertAIViewModel(): ViewModel() {

    var showOverview: Boolean by mutableStateOf(true)
            private set


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TaskConvertAIViewModel()
            }
        }
    }
}