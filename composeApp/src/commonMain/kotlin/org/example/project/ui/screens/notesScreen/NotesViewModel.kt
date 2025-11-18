package org.example.project.ui.screens.notesScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import org.example.project.AppDependencies
import org.example.project.data.analyzer.AnalyzerRepository

class NotesViewModel (
    private val analyzerRepository: AnalyzerRepository
): ViewModel() {


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val analyzerRepository = AppDependencies.container.analyzerRepository
                NotesViewModel(analyzerRepository = analyzerRepository)
            }
        }
    }
}