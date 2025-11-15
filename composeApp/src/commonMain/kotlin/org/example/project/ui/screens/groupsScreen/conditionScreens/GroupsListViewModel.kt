package org.example.project.ui.screens.groupsScreen.states

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import androidx.compose.ui.graphics.Color


data class GroupItem(
    val id: Int,
    val name: String,
    val description: String,
    val membersCount: Int,
    val notesCount: Int,
    val bottomBorderColor: Color
)

open class GroupsViewModel : ViewModel() {

    private val _groups = MutableStateFlow(sampleGroups())
    val groups: StateFlow<List<GroupItem>> = _groups

    fun onGroupClick(id: Int) {
        // здесь потом будет логика навигации или загрузки данных
        println("Group clicked: $id")
    }

    private fun sampleGroups(): List<GroupItem> = listOf(
        GroupItem(
            id = 1,
            name = "Product Squad",
            description = "Планирование, приоритезация задач и контроль пользовательского опыта",
            membersCount = 3,
            notesCount = 5,
            bottomBorderColor = Color(0xFF3D5AFE)
        ),
        GroupItem(
            id = 2,
            name = "Project Orbit",
            description = "Группа для обсуждения продуктовых решений, задач и пользовательских сценариев",
            membersCount = 10,
            notesCount = 15,
            bottomBorderColor = Color(0xFF3D5AFE)
        ),
        GroupItem(
            id = 3,
            name = "Frontend Crew",
            description = "Разработка интерфейсов, UI-компонентов и интеграция с API",
            membersCount = 4,
            notesCount = 7,
            bottomBorderColor = Color(0xFF3D5AFE)
        )
    )
}
