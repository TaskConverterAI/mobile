package org.example.project.ui.screens.groupsScreen.creatingGroupScreens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.AppDependencies
import org.example.project.data.auth.AuthRepository
import org.example.project.data.database.repository.GroupRepository
import org.example.project.data.commonData.Group
import org.example.project.data.commonData.Privileges
import org.example.project.data.commonData.User

data class CreateGroupUiState(
    val groupName: String = "",
    val description: String = "",
    val participants: List<String> = emptyList(),
    val addDialogVisible: Boolean = false,
    val newEmail: String = "",
    val emailError: String? = null,
    val error: String? = null
)

class CreateGroupViewModel(
    private val groupRepository: GroupRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

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

    fun createGroup(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            // TODO: нужно добавить участников
            try {
                Logger.d {"Creating group: ${uiState.value.groupName}"}

                val refreshRes = authRepository.refresh()

                if (!refreshRes) {
                    throw RuntimeException("Refresh error")
                }

                val userData = authRepository.decode() ?: throw RuntimeException("Decode error")
                val userMembers:  MutableList<User> = mutableListOf()

                val group = groupRepository.createGroup(
                    Group(0,
                    uiState.value.groupName,
                    uiState.value.description,
                    0, 0,
                    userMembers, 0),
                    userData.first)

                if (group != null) {
                    for (emailOrName in uiState.value.participants) {
                        groupRepository.addMemberInGroup(
                            group.id,
                            emailOrName,
                            Privileges.member)
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            error = "Не удалось создать группу"
                        )
                    }
                }

                onComplete()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Возникла ошибка при создании группы"
                    )
                }
                Logger.e(e) { "Failed to create group" }
            }
        }
    }

    fun clearError() {
        _uiState.update{it.copy(
            error = null
        )}
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val groupRepository = AppDependencies.container.groupRepository
                val authRepository = AppDependencies.container.authRepository
                CreateGroupViewModel(groupRepository = groupRepository, authRepository)
            }
        }
    }

}

fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
    return email.isNotEmpty() && emailRegex.matches(email)
}

