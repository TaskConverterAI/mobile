package org.example.project.ui.screens.groupsScreen.conditionScreens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.AppDependencies
import org.example.project.data.commonData.Group
import org.example.project.data.database.repository.GroupRepository
import kotlin.collections.emptyList

data class GroupListUi(
    val showBottom: Boolean = true,
    val isEmptyList: Boolean = true
)

open class GroupsViewModel(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _listUi = MutableStateFlow(GroupListUi())
    val listUi: StateFlow<GroupListUi> = _listUi.asStateFlow()
    private val _groups = MutableStateFlow(emptyList<Group>())
    val groups: StateFlow<List<Group>> = _groups

    fun onGroupClick(id: Int) {
        println("Group clicked: $id")
    }

    fun setBottom(isBottom: Boolean) {
        _listUi.update { currentState ->
            currentState.copy(showBottom = isBottom)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val groupRepository = AppDependencies.container.groupRepository
                GroupsViewModel(groupRepository = groupRepository)
            }
        }
    }

}
