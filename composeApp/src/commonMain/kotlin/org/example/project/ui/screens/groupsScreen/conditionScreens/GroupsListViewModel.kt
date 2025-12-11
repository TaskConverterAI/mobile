package org.example.project.ui.screens.groupsScreen.conditionScreens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.AppDependencies
import org.example.project.data.auth.AuthRepository
import org.example.project.data.commonData.Group
import org.example.project.data.commonData.Note
import org.example.project.data.database.repository.GroupRepository
import kotlin.collections.emptyList

data class GroupListUi(
    val showBottom: Boolean = false,
    val isEmptyList: Boolean = true,
    val groups: List<Group> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedGroup: Group? = null
)

open class GroupsViewModel(
    private val groupRepository: GroupRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _listUi = MutableStateFlow(GroupListUi())
    val listUi: StateFlow<GroupListUi> = _listUi.asStateFlow()

    fun onGroupClick(id: Int) {
        println("Group clicked: $id")
    }

    fun setBottom(isBottom: Boolean) {
        _listUi.update { currentState ->
            currentState.copy(showBottom = isBottom)
        }
    }

    fun loadGroups() {
        Logger.d("GroupsViewModel: loadGroups called")

        viewModelScope.launch {
            _listUi.update { it.copy(isLoading = true, error = null) }
            Logger.d("GroupsViewModel: Loading state set to true")

            try {
                val refreshRes = authRepository.refresh()

                if (!refreshRes) {
                    throw RuntimeException("Refresh error")
                }

                val userData = authRepository.decode() ?: throw RuntimeException("Decode error")

                val groups = groupRepository.getAllGroups(userData.first)
                Logger.d("GroupsViewModel: Received ${groups?.size ?: 0} groups from repository")

                _listUi.update { currentState ->
                    currentState.copy(
                        groups = groups ?: emptyList(),
                        isEmptyList = groups.isNullOrEmpty(),
                        isLoading = false,
                        error = null
                    )
                }

                Logger.d("GroupsViewModel: UI state updated, isEmptyList = ${groups.isNullOrEmpty()}")
            } catch (e: Exception) {
                Logger.e("GroupsViewModel: Exception caught - ${e.message}")

                _listUi.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                ) }
            }
        }
    }

    /**
     * Получить конкретную группу по ID
     * @param groupId - ID группы
     * @return Group с полными деталями
     */
    suspend fun getGroupById(groupId: Long): Group? {
        return try {
            groupRepository.getGroupById(groupId = groupId)
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val groupRepository = AppDependencies.container.groupRepository
                val authRepository = AppDependencies.container.authRepository
                GroupsViewModel(groupRepository = groupRepository, authRepository = authRepository)
            }
        }
    }

}
