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
    val isGroupNameCorrect: Boolean = true,
    val groupNameErrMsg: String = "",
    val isDescriptionCorrect: Boolean = true,
    val descriptionErrMsg: String = "",
    val participantsErrMsg: String = "",
    val isLoading: Boolean = false,
    val isCheckingUser: Boolean = false,
    val isCheckingGroupName: Boolean = false,
    val isGroupNameUnique: Boolean = true
)

class CreateGroupViewModel(
    private val groupRepository: GroupRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateGroupUiState())
    val uiState: StateFlow<CreateGroupUiState> = _uiState

    // --------------- UPDATE FIELD VALUES ---------------

    fun onGroupNameChange(value: String) {
        validateGroupName(value)
    }

    fun onDescriptionChange(value: String) {
        validateDescription(value)
    }

    fun removeParticipant(index: Int) {
        _uiState.update {
            it.copy(
                participants = it.participants.toMutableList().apply {
                    removeAt(index)
                },
                participantsErrMsg = ""
            )
        }
    }

    fun openAddDialog() {
        _uiState.update { it.copy(addDialogVisible = true, newEmail = "", emailError = null) }
    }

    fun closeAddDialog() {
        _uiState.update { it.copy(addDialogVisible = false, newEmail = "", emailError = null) }
    }

    fun onNewEmailChange(value: String) {
        validateEmail(value)
    }

    fun addParticipant() {
        val state = _uiState.value
        val trimmedEmail = state.newEmail.trim()

        // Проверяем все условия для добавления
        if (state.emailError != null || trimmedEmail.isEmpty()) {
            return
        }

        if (state.participants.size >= 50) {
            _uiState.update { it.copy(participantsErrMsg = "Максимальное количество участников: 50") }
            return
        }

        if (state.participants.contains(trimmedEmail)) {
            _uiState.update {
                it.copy(emailError = "Пользователь уже добавлен в группу")
            }
            return
        }

        // Добавляем участника в список
        _uiState.update {
            it.copy(
                participants = it.participants + trimmedEmail,
                addDialogVisible = false,
                newEmail = "",
                emailError = null,
                participantsErrMsg = ""
            )
        }
    }

    // --------------- VALIDATION ---------------

    fun validateEmail(input: String) {
        val trimmedInput = input.trim()
        var errorMsg: String? = null

        when {
            trimmedInput.isEmpty() -> {
                // Пустое поле - не ошибка, просто очищаем
                errorMsg = null
            }
            !isValidEmailOrUsername(trimmedInput) -> {
                errorMsg = if (trimmedInput.contains("@")) {
                    "Некорректный формат email"
                } else {
                    "Username должен содержать только буквы, цифры, _ или - (3-50 символов)"
                }
            }
            _uiState.value.participants.contains(trimmedInput) -> {
                errorMsg = "Пользователь уже добавлен в группу"
            }
        }

        _uiState.update {
            it.copy(
                newEmail = input,
                emailError = errorMsg
            )
        }
    }

    fun validateGroupName(groupName: String): Boolean {
        val trimmedName = groupName.trim()
        var errorMsg = ""
        var isCorrect = true

        if (trimmedName.isEmpty()) {
            errorMsg = "Название группы не может быть пустым"
            isCorrect = false
        } else if (trimmedName.length > 255) {
            errorMsg = "Название группы не должно превышать 255 символов"
            isCorrect = false
        }

        _uiState.update { currentState ->
            currentState.copy(
                groupName = groupName,
                isGroupNameCorrect = isCorrect,
                groupNameErrMsg = errorMsg
            )
        }

        // Если базовая валидация прошла, проверяем уникальность
        if (isCorrect && trimmedName.isNotEmpty()) {
            checkGroupNameUniqueness(trimmedName)
        }

        return isCorrect
    }

    private fun checkGroupNameUniqueness(groupName: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isCheckingGroupName = true) }

                val refreshRes = authRepository.refresh()
                if (!refreshRes) {
                    throw RuntimeException("Refresh error")
                }

                val userData = authRepository.decode() ?: throw RuntimeException("Decode error")
                val existingGroups = groupRepository.getAllGroups(userData.first)

                val isDuplicate = existingGroups?.any { group ->
                    group.name.equals(groupName, ignoreCase = true)
                } ?: false

                _uiState.update { currentState ->
                    currentState.copy(
                        isCheckingGroupName = false,
                        isGroupNameUnique = !isDuplicate,
                        groupNameErrMsg = if (isDuplicate) "Группа с таким названием уже существует" else currentState.groupNameErrMsg,
                        isGroupNameCorrect = currentState.isGroupNameCorrect && !isDuplicate
                    )
                }
            } catch (e: Exception) {
                Logger.e(e) { "Failed to check group name uniqueness" }
                _uiState.update {
                    it.copy(
                        isCheckingGroupName = false,
                        // В случае ошибки проверки не блокируем создание
                        isGroupNameUnique = true
                    )
                }
            }
        }
    }

    fun validateDescription(description: String): Boolean {
        var errorMsg = ""
        var isCorrect = true

        if (description.length > 1000) {
            errorMsg = "Описание не должно превышать 1000 символов"
            isCorrect = false
        }

        _uiState.update { currentState ->
            currentState.copy(
                description = description,
                isDescriptionCorrect = isCorrect,
                descriptionErrMsg = errorMsg
            )
        }

        return isCorrect
    }

    fun canCreateGroup(): Boolean {
        val state = _uiState.value
        return state.isGroupNameCorrect &&
               state.isGroupNameUnique &&
               state.isDescriptionCorrect &&
               state.groupName.trim().isNotEmpty() &&
               state.participantsErrMsg.isEmpty() &&
               !state.isLoading &&
               !state.isCheckingGroupName
    }

    fun clearParticipantsError() {
        _uiState.update { it.copy(participantsErrMsg = "") }
    }

    // Предварительная проверка участников без создания группы
    fun preValidateParticipants() {
        if (uiState.value.participants.isEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isCheckingUser = true, participantsErrMsg = "") }

                Logger.d { "Pre-validating ${uiState.value.participants.size} participants" }

                val refreshRes = authRepository.refresh()
                if (!refreshRes) {
                    throw RuntimeException("Refresh error")
                }

                val userData = authRepository.decode() ?: throw RuntimeException("Decode error")

                // Создаем временную группу для проверки
                val tempGroup = groupRepository.createGroup(
                    Group(0, "temp_validation_group_${System.currentTimeMillis()}", "", 0, 0, mutableListOf(), 0),
                    userData.first
                )

                if (tempGroup != null) {
                    val failedUsers = mutableListOf<String>()

                    // Пробуем добавить всех участников
                    for (emailOrName in uiState.value.participants) {
                        val addedUser = groupRepository.addMemberInGroup(
                            tempGroup.id,
                            emailOrName,
                            Privileges.member
                        )

                        if (addedUser == null) {
                            failedUsers.add(emailOrName)
                        }
                    }

                    // Удаляем временную группу
                    try {
                        groupRepository.deleteGroup(tempGroup.id, userData.first)
                    } catch (e: Exception) {
                        Logger.e(e) { "Failed to delete temp validation group" }
                    }

                    // Обновляем состояние с результатами
                    if (failedUsers.isNotEmpty()) {
                        val errorMessage = if (failedUsers.size == 1) {
                            "Пользователь ${failedUsers.first()} не найден или недоступен"
                        } else {
                            "Недоступные пользователи: ${failedUsers.joinToString(", ")}"
                        }

                        _uiState.update {
                            it.copy(
                                isCheckingUser = false,
                                participantsErrMsg = errorMessage
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isCheckingUser = false,
                                participantsErrMsg = ""
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                Logger.e(e) { "Failed to pre-validate participants" }
                _uiState.update {
                    it.copy(
                        isCheckingUser = false,
                        participantsErrMsg = "Ошибка при проверке участников: ${e.message}"
                    )
                }
            }
        }
    }

    fun createGroup(onComplete: () -> Unit = {}) {
        // Строгая проверка всех условий - если что-то не в порядке, не создаем группу
        if (!canCreateGroup()) {
            Logger.w { "Cannot create group - validation failed" }
            return
        }

        viewModelScope.launch {
            try {
                // Включаем индикатор загрузки
                _uiState.update { it.copy(isLoading = true) }

                Logger.d {"Creating group: ${uiState.value.groupName}"}

                val refreshRes = authRepository.refresh()
                if (!refreshRes) {
                    throw RuntimeException("Refresh error")
                }

                val userData = authRepository.decode() ?: throw RuntimeException("Decode error")

                // СНАЧАЛА проверяем всех участников, создав временную группу
                if (uiState.value.participants.isNotEmpty()) {
                    Logger.d { "Pre-validating ${uiState.value.participants.size} participants" }

                    // Создаем временную группу для проверки участников
                    val tempGroup = groupRepository.createGroup(
                        Group(0,
                        uiState.value.groupName.trim(),
                        uiState.value.description,
                        0, 0,
                        mutableListOf(), 0),
                        userData.first)

                    if (tempGroup != null) {
                        val failedUsers = mutableListOf<String>()

                        // Пробуем добавить всех участников
                        for (emailOrName in uiState.value.participants) {
                            val addedUser = groupRepository.addMemberInGroup(
                                tempGroup.id,
                                emailOrName,
                                Privileges.member
                            )

                            if (addedUser == null) {
                                failedUsers.add(emailOrName)
                            }
                        }

                        // Если есть недоступные участники - УДАЛЯЕМ группу и показываем ошибку
                        if (failedUsers.isNotEmpty()) {
                            Logger.w { "Failed participants detected: $failedUsers. Deleting temp group." }

                            // Удаляем временную группу
                            try {
                                groupRepository.deleteGroup(tempGroup.id, userData.first)
                            } catch (e: Exception) {
                                Logger.e(e) { "Failed to delete temp group" }
                            }

                            val errorMessage = if (failedUsers.size == 1) {
                                "Пользователь ${failedUsers.first()} не найден или недоступен. Группа не создана."
                            } else {
                                "Недоступные пользователи: ${failedUsers.joinToString(", ")}. Группа не создана."
                            }

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    participantsErrMsg = errorMessage
                                )
                            }
                            return@launch
                        }

                        // Все участники добавились успешно - группа готова!
                        Logger.d { "All participants validated successfully. Group created: ${tempGroup.name}" }

                        _uiState.update { it.copy(isLoading = false) }
                        onComplete()

                    } else {
                        throw RuntimeException("Failed to create group on server")
                    }
                } else {
                    // Нет участников - создаем группу сразу
                    val group = groupRepository.createGroup(
                        Group(0,
                        uiState.value.groupName.trim(),
                        uiState.value.description,
                        0, 0,
                        mutableListOf(), 0),
                        userData.first)

                    if (group != null) {
                        Logger.d { "Group without participants created successfully: ${group.name}" }
                        _uiState.update { it.copy(isLoading = false) }
                        onComplete()
                    } else {
                        throw RuntimeException("Failed to create group on server")
                    }
                }

            } catch (e: Exception) {
                Logger.e(e) { "Failed to create group" }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        participantsErrMsg = "Ошибка при создании группы: ${e.message}"
                    )
                }
            }
        }
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
    if (email.isEmpty()) return false

    // Более строгий регекс для email валидации
    val emailRegex = Regex(
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
    )

    return emailRegex.matches(email.trim()) &&
           email.length <= 254 && // RFC 5321 ограничение
           !email.startsWith(".") &&
           !email.endsWith(".") &&
           !email.contains("..") // Избегаем двойных точек
}

fun isValidUsername(username: String): Boolean {
    if (username.isEmpty()) return false

    val trimmedUsername = username.trim()

    // Валидация username: только буквы, цифры, подчеркивание, дефис
    val usernameRegex = Regex("^[a-zA-Z0-9_-]+$")

    return usernameRegex.matches(trimmedUsername) &&
           trimmedUsername.length >= 3 &&  // Минимум 3 символа
           trimmedUsername.length <= 50 && // Максимум 50 символов
           !trimmedUsername.startsWith("-") &&
           !trimmedUsername.endsWith("-") &&
           !trimmedUsername.startsWith("_") &&
           !trimmedUsername.endsWith("_")
}

fun isValidEmailOrUsername(input: String): Boolean {
    if (input.isEmpty()) return false

    val trimmedInput = input.trim()

    // Если содержит @, то валидируем как email
    return if (trimmedInput.contains("@")) {
        isValidEmail(trimmedInput)
    } else {
        // Иначе валидируем как username
        isValidUsername(trimmedInput)
    }
}

