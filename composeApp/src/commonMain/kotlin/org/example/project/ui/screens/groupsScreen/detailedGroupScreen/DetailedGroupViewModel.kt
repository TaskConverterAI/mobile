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

data class GroupUiDetails(
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
    val error: String? = null,
    val successLeave: Boolean = false
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
                    val isAdmin = userId == group.ownerId
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
                    val userId = authRepository.getUserIdByToken()

                    if (userToRemove.id == userId) {
                        _groupDetails.update { it.copy(showLeaveDialog = true) }
                        return@launch
                    }

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
                            showAddMemberDialog = false
                        )

                    }
                    Logger.d("MY_APP_TAG") { "User ${addedMember.email} added to group successfully" }

                } else {
                    _groupDetails.update { it.copy(error = "Не удалось добавить участника") }
                }
            } catch (e: Exception) {
                Logger.e("DetailedGroupViewModel", e) { "Error adding participant: ${e.message}" }
                _groupDetails.update { it.copy(error = "Произошла внутренняя ошибка, попробуйте позже") }
            }
        }
    }

    fun dismissAddMemberDialog() {
        _groupDetails.update { currentState ->
            currentState.copy(showAddMemberDialog = false)
        }
    }

    fun setLeave(leave: Boolean) {
        _groupDetails.update { currentState ->
            currentState.copy(showLeaveDialog = leave)
        }
    }

    fun leaveMyOwnGroup(accessorEmail: String) {
        viewModelScope.launch {
            try {
                val groupId = _groupDetails.value.groupId
                val userId = authRepository.getUserIdByToken()
                var accessorId: Long? = null

                groupDetails.value.users.forEach { member ->
                    if (member.email == accessorEmail)
                        accessorId = member.id
                }

                if (accessorId == null) {
                    throw Exception("Наследник не найден")
                }
                setLeave(false)
                // Вызываем API для выхода из группы
                if (groupRepository.leaveGroup(groupId, userId, accessorId)) {
                    Logger.d { "User left the group successfully" }
                    _groupDetails.update { it.copy(successLeave = true) }
                } else {
                    throw Exception("Внутренняя ошибка")
                }
            } catch (e: Exception) {
                Logger.e("DetailedGroupViewModel", e) { "Error leaving group: ${e.message}" }
                _groupDetails.update { it.copy(error = "Ошибка при выходе из группы: ${e.message}", showLeaveDialog = false) }
            }
        }
    }

    fun leaveGroup() {
        setLeave(false)
        viewModelScope.launch {
            try {
                val groupId = _groupDetails.value.groupId
                val userId = authRepository.getUserIdByToken()
                Logger.d("DetailedGroupViewModel") { "Start leaving" }

                if (groupDetails.value.ownerId == userId ) {
                    groupRepository.deleteGroup(groupId, userId)
                    _groupDetails.update { it.copy(successLeave = true) }
                    return@launch
                }
                // Вызываем API для выхода из группы
                if (groupRepository.leaveGroup(groupId, userId, userId)) {
                    Logger.d("DetailedGroupViewModel") { "User left the group successfully" }
                    _groupDetails.update { it.copy(successLeave = true) }
                } else {
                    throw Exception("Внутренняя ошибка")
                }
            } catch (e: Exception) {
                Logger.e("DetailedGroupViewModel", e) { "Error leaving group: ${e.message}" }
                _groupDetails.update { it.copy(error = "Ошибка при выходе из группы") }
            }
        }
    }

    fun clearError() {
        _groupDetails.update { it.copy(error = null) }
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
