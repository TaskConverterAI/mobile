package org.example.project.ui.screens.groupsScreen.states

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.AppDependencies
import org.example.project.data.commonData.Group
import org.example.project.data.commonData.Privileges
import org.example.project.data.commonData.User
import org.example.project.data.groups.GroupRepository
import org.example.project.ui.screens.auth.AuthViewModel

data class GroupListUi(
    val showBottom: Boolean = true,
    val isEmptyList: Boolean = true
)

open class GroupsViewModel(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _listUi = MutableStateFlow(GroupListUi())
    val listUi: StateFlow<GroupListUi> = _listUi.asStateFlow()
    private val _groups = MutableStateFlow(sampleGroups())
    val groups: StateFlow<List<Group>> = _groups

    fun onGroupClick(id: Int) {
        println("Group clicked: $id")
    }

    fun setBottom(isBottom: Boolean) {
        _listUi.update { currentState ->
            currentState.copy(showBottom = isBottom)
        }
    }

    private fun sampleGroups(): List<Group> {
        return listOf(
            Group(
                id = 1,
                name = "Product Squad",
                description = "Планирование, приоритезация задач и контроль пользовательского опыта",
                users = listOf(
                    User("alice@example.com", Privileges.FULL),
                    User("bob@example.com", Privileges.PART),
                    User("carol@example.com", Privileges.PART)
                ),
                noteIds = listOf(1, 2, 3, 4, 5)
            ),
            Group(
                id = 2,
                name = "Project Orbit",
                description = "Группа для обсуждения продуктовых решений, задач и пользовательских сценариев",
                users = listOf(
                    User("dave@example.com", Privileges.FULL),
                    User("eve@example.com", Privileges.PART),
                    User("frank@example.com", Privileges.PART),
                    User("grace@example.com", Privileges.FULL),
                    User("heidi@example.com", Privileges.PART),
                    User("ivan@example.com", Privileges.PART),
                    User("judy@example.com", Privileges.PART),
                    User("mallory@example.com", Privileges.FULL),
                    User("oscar@example.com", Privileges.PART),
                    User("peggy@example.com", Privileges.PART)
                ),
                noteIds = listOf(6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
            ),
            Group(
                id = 3,
                name = "Frontend Crew",
                description = "Разработка интерфейсов, UI-компонентов и интеграция с API",
                users = listOf(
                    User("trent@example.com", Privileges.FULL),
                    User("victor@example.com", Privileges.PART),
                    User("wendy@example.com", Privileges.PART),
                    User("xavier@example.com", Privileges.PART)
                ),
                noteIds = listOf(16, 17, 18, 19, 20, 21, 22)
            )
        )
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
