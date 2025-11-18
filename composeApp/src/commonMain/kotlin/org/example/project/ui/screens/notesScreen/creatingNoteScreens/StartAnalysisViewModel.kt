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
import org.example.project.model.GeoLocation
import org.example.project.model.TaskRequest

class StartAnalysisViewModel(
    private val authRepository: AuthRepository,
    private val analyzerRepository: AnalyzerRepository
) : ViewModel() {

    private var _jobId: String = ""
    private var _text: String = ""
    private var _hints: String = ""

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    fun updateName(newName: String) {
        _name.update { newName }
    }

    private val _location = MutableStateFlow("")
    val location = _location.asStateFlow()

    fun updateLocation(newLocation: String) {
        _location.update { newLocation }
    }

    var latitude: Double? = null
        private set

    var longitude: Double? = null
        private set

    fun updateCoords(lat: Double?, lon: Double?) {
        latitude = lat
        longitude = lon
    }

    private val _group = MutableStateFlow("")
    val group = _group.asStateFlow()

    fun updateGroup(newGroup: String) {
        _group.update { newGroup }
    }

    private val _date = MutableStateFlow("")
    val date = _date.asStateFlow()

    fun updateDate(newDate: String) {
        _date.update { newDate }
    }


    fun loadArgs(jobId: String, text: String, hints: String) {
        _jobId = jobId
        _text = text
        _hints = hints
    }

    fun startAnalysis() {
        var geo: GeoLocation? = null

        if (latitude != null && longitude != null) {
            geo = GeoLocation(latitude!!, longitude!!)
        }

        viewModelScope.launch {
            analyzerRepository.analyzeText(
                authRepository.getUserId(),
                TaskRequest(
                    description = _text,
                    geo = geo,
                    name = _name.value,
                    group = _group.value,
                    data = _hints,
                    date = _date.value
                )
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authRepository = AppDependencies.container.authRepository
                val analyzerRepository = AppDependencies.container.analyzerRepository

                StartAnalysisViewModel(
                    authRepository = authRepository,
                    analyzerRepository = analyzerRepository
                )
            }
        }
    }
}