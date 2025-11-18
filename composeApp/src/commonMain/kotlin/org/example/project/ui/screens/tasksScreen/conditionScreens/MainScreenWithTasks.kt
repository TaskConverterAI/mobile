package org.example.project.ui.screens.tasksScreen.conditionScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.example.project.data.commonData.Task
import org.example.project.ui.viewComponents.commonComponents.BlockType
import org.example.project.ui.viewComponents.commonComponents.ColorBlock

import org.example.project.ui.viewComponents.commonComponents.FilterSelector
import org.example.project.ui.viewmodels.TasksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithTasks(navController: NavController, viewModel: TasksViewModel) {
    var selectedFilter by remember { mutableStateOf("Все группы") }
    val filterOptions = listOf("Все группы")
    var selectedStatus by remember { mutableStateOf("Все статусы") }
    val statusOptions = listOf("Все статусы")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp)
    ) {
        Text(
            text = "Задачи",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 5.dp, start = 10.dp).scale(1.1F)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterSelector(
                selectedFilter = selectedFilter,
                filterOptions = filterOptions,
                onFilterSelected = { selectedFilter = it },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            FilterSelector(
                selectedFilter = selectedStatus,
                filterOptions = statusOptions,
                onFilterSelected = { selectedStatus = it },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        val tasks by viewModel.tasks.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tasks.forEach { task ->
                ColorBlock(BlockType.ADVANCED_TASK, task, backgroundColor = MaterialTheme.colorScheme.primary, navController = navController)
            }
        }
    }
}
