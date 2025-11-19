package org.example.project.ui.screens.groupsScreen.detailedGroupScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.project.AppDependencies
import org.example.project.data.commonData.User
import org.example.project.data.database.repository.GroupRepository

data class GroupUiDetails (
    val name: String = "",
    val showLeaveDialog: Boolean = false,
    val isAdmin: Boolean = false,
    val users: List<User> = listOf()
)

open class DetailedGroupViewModel(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _groupDetails = MutableStateFlow(GroupUiDetails())
    val groupDetails: StateFlow<GroupUiDetails> = _groupDetails.asStateFlow()


    fun setGroup(groupName: String) {
        _groupDetails.update { currentState ->
            currentState.copy(name = groupName)
        }
    }

    fun removeParticipant(idx: Int) {
        // TODO
    }

    fun addParticipant() {
        // TODO
    }

    fun setLeave(leave: Boolean) {
        _groupDetails.update { currentState ->
            currentState.copy(showLeaveDialog = leave)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val groupRepository = AppDependencies.container.groupRepository
                DetailedGroupViewModel(
                    groupRepository = groupRepository,
                )
            }
        }
    }
}
