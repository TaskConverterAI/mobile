package org.example.project.ui.screens.groupsScreen.creatingGroupScreens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateGroupUiState(
    val groupName: String = "",
    val description: String = "",
    val participants: List<String> = emptyList(),
    val addDialogVisible: Boolean = false,
    val newEmail: String = "",
    val emailError: String? = null
)

class CreateGroupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateGroupUiState())
    val uiState: StateFlow<CreateGroupUiState> = _uiState

    // --------------- UPDATE FIELD VALUES ---------------

    fun onGroupNameChange(value: String) {
        _uiState.update { it.copy(groupName = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun removeParticipant(index: Int) {
        _uiState.update {
            it.copy(participants = it.participants.toMutableList().apply {
                removeAt(index)
            })
        }
    }

    fun openAddDialog() {
        _uiState.update { it.copy(addDialogVisible = true, newEmail = "", emailError = null) }
    }

    fun closeAddDialog() {
        _uiState.update { it.copy(addDialogVisible = false, newEmail = "", emailError = null) }
    }

    fun onNewEmailChange(value: String) {
        _uiState.update {
            it.copy(
                newEmail = value,
                emailError = if (isValidEmail(value)) null else "Некорректный email"
            )
        }
    }

    fun addParticipant() {
        val state = _uiState.value
        if (state.emailError == null && state.newEmail.isNotBlank()) {
            _uiState.update {
                it.copy(
                    participants = it.participants + it.newEmail,
                    addDialogVisible = false,
                    newEmail = ""
                )
            }
        }
    }

    // --------------- ACTION: CREATE GROUP ---------------

    fun createGroup(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            // TODO: логика создания группы
            onComplete()
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
    return email.isNotEmpty() && emailRegex.matches(email)
}

