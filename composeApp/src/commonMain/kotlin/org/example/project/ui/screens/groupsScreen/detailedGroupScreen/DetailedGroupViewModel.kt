package org.example.project.ui.screens.groupsScreen.detailedGroupScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.touchlab.kermit.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.AppDependencies
import org.example.project.data.auth.AuthRepository
import org.example.project.data.commonData.Privileges
import org.example.project.data.commonData.User
import org.example.project.data.database.repository.GroupRepository

data class GroupUiDetails (
    val groupId: Long = 0L,
    val name: String = "",
    val description: String = "",
    val showLeaveDialog: Boolean = false,
    val showAddMemberDialog: Boolean = false,
    val isAdmin: Boolean = false,
    val isOwner: Boolean = false,
    val ownerId: Long = 0L,
    val users: List<User> = listOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)

open class DetailedGroupViewModel(
    private val groupRepository: GroupRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _groupDetails = MutableStateFlow(GroupUiDetails())
    val groupDetails: StateFlow<GroupUiDetails> = _groupDetails.asStateFlow()

    fun loadGroup(groupId: Long) {
        viewModelScope.launch {
            _groupDetails.update { it.copy(isLoading = true, error = null) }

            try {
                // Получаем ID текущего пользователя
                val userId = authRepository.getUserIdByToken()

                // Загружаем данные группы
                val group = groupRepository.getGroupById(groupId)

                if (group != null) {
                    // Определяем, является ли текущий пользователь админом или владельцем
                    val currentUserInGroup = group.members.find { it.id == userId }
                    val isAdmin = currentUserInGroup?.privileges == Privileges.admin
                    val isOwner = group.ownerId == userId

                    _groupDetails.update {
                        GroupUiDetails(
                            groupId = group.id,
                            name = group.name,
                            description = group.description,
                            users = group.members,
                            isAdmin = isAdmin || isOwner, // Владелец также имеет права админа
                            isOwner = isOwner,
                            ownerId = group.ownerId,
                            isLoading = false,
                            error = null
                        )
                    }

                    Logger.d { "Group loaded: ${group.name}, members: ${group.members.size}, isAdmin: ${isAdmin || isOwner}" }
                } else {
                    _groupDetails.update {
                        it.copy(
                            isLoading = false,
                            error = "Группа не найдена"
                        )
                    }
                    Logger.e { "Group not found: $groupId" }
                }
            } catch (e: Exception) {
                _groupDetails.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Неизвестная ошибка"
                    )
                }
                Logger.e("DetailedGroupViewModel", e) { "Error loading group: ${e.message}" }
            }
        }
    }

    fun removeParticipant(idx: Int) {
        viewModelScope.launch {
            try {
                val currentUsers = _groupDetails.value.users
                if (idx in currentUsers.indices) {
                    val userToRemove = currentUsers[idx]
                    val groupId = _groupDetails.value.groupId

                    // Вызываем API для удаления участника
                    groupRepository.deleteMemberFromGroup(groupId, userToRemove.id)

                    // Обновляем локальное состояние
                    _groupDetails.update { currentState ->
                        currentState.copy(
                            users = currentUsers.filterIndexed { index, _ -> index != idx },
                            error = "Пользователь успешно удален"
                        )
                    }

                    Logger.d { "User ${userToRemove.email} removed from group" }
                }
            } catch (e: Exception) {
                Logger.e("DetailedGroupViewModel", e) { "Error removing participant: ${e.message}" }
                _groupDetails.update { it.copy(error = "Ошибка при удалении участника") }
            }
        }
    }

    fun addParticipant() {
        _groupDetails.update { currentState ->
            currentState.copy(showAddMemberDialog = true)
        }
    }

    fun addParticipantByEmail(email: String) {
        viewModelScope.launch {
            try {
                _groupDetails.update { currentState ->
                    currentState.copy(
                        showAddMemberDialog = false
                    )

                }
                val groupId = _groupDetails.value.groupId

                // Вызываем API для добавления участника по email
                val addedMember = groupRepository.addMemberInGroup(groupId, email)

                if (addedMember != null) {
                    // Обновляем локальное состояние, добавляя нового участника
                    _groupDetails.update { currentState ->
                        currentState.copy(
                            users = currentState.users + addedMember,
                            showAddMemberDialog = false,
                            error = "Пользователь успешно добавлен в группу"
                        )

                    }
                    Logger.d { "User ${addedMember.email} added to group successfully" }

                } else {
                    _groupDetails.update { it.copy(error = "Не удалось добавить участника") }
                }
            } catch (e: Exception) {
                Logger.e("DetailedGroupViewModel", e) { "Error adding participant: ${e.message}" }
                _groupDetails.update { it.copy(error = "Ошибка при добавлении участника: ${e.message}") }
            }
        }
    }

    fun dismissAddMemberDialog() {
        _groupDetails.update { currentState ->
            currentState.copy(showAddMemberDialog = false)
        }
    }

    fun dismissLeaveGroupDialog() {
        _groupDetails.update { currentState ->
            currentState.copy(showLeaveDialog = true)
        }
    }

    fun setLeave(leave: Boolean) {
        _groupDetails.update { currentState ->
            currentState.copy(showLeaveDialog = leave)
        }
    }

    fun leaveGroup() {
        viewModelScope.launch {
            try {
                val groupId = _groupDetails.value.groupId
                val userId = authRepository.getUserIdByToken()

                // Вызываем API для выхода из группы
                groupRepository.leaveGroup(groupId, userId)

                Logger.d { "User left the group successfully" }
                // Здесь можно добавить навигацию назад или обновление UI
            } catch (e: Exception) {
                Logger.e("DetailedGroupViewModel", e) { "Error leaving group: ${e.message}" }
                _groupDetails.update { it.copy(error = "Ошибка при выходе из группы") }
            }
        }
    }

    fun clearError(time: Long) {

        viewModelScope.launch {
            delay(time) // 5 секунд
            _groupDetails.update { it.copy(error = null) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val groupRepository = AppDependencies.container.groupRepository
                val authRepository = AppDependencies.container.authRepository
                DetailedGroupViewModel(
                    groupRepository = groupRepository,
                    authRepository = authRepository
                )
            }
        }
    }
}
