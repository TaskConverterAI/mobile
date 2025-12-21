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

import org.example.project.ui.viewComponents.commonComponents.BlockType
import org.example.project.ui.viewComponents.commonComponents.ColorBlock
import org.example.project.ui.viewComponents.commonComponents.FilterSelector
import org.example.project.data.commonData.Status

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithTasks(navController: NavController, taskJob: org.example.project.ui.screens.tasksScreen.TasksViewModel, allTasks: org.example.project.ui.viewmodels.TasksViewModel) {
    var selectedFilter by remember { mutableStateOf("Все группы") }
    val filterOptions = listOf("Все группы")

    var selectedStatus by remember { mutableStateOf("Все статусы") }
    val statusOptions = listOf("Все статусы", "Активные", "Выполненные")

    val jobs by taskJob.currentJobs.collectAsState()
    val tasks by allTasks.tasks.collectAsState()

    // Применяем фильтр по статусам
    val filteredTasks = remember(tasks, selectedStatus) {
        when (selectedStatus) {
            "Активные" -> tasks.filter { it.status == Status.UNDONE }
            "Выполненные" -> tasks.filter { it.status == Status.DONE }
            else -> tasks
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Jobs section
        jobs.forEach { job ->
            ColorBlock(
                BlockType.JOB,
                job = job,
                backgroundColor = MaterialTheme.colorScheme.surface,
                navController = navController,
                onCloseErrorClick = {
                    taskJob.closeErrorMsg(job)
                }
            )
        }

        // Tasks section header
        Text(
            text = "Задачи",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 5.dp, start = 10.dp, top = 16.dp).scale(1.1F)
        )

        // Filters
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

        // Tasks list (с учётом фильтра)
        filteredTasks.forEach { task ->
            ColorBlock(
                BlockType.ADVANCED_TASK,
                task,
                backgroundColor = MaterialTheme.colorScheme.primary,
                navController = navController
            )
        }
    }
}
