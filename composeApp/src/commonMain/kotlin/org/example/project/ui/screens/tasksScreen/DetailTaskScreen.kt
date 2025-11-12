package org.example.project.ui.screens.tasksScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.serialization.Serializable

import org.example.project.data.commonData.Priority
import org.example.project.data.commonData.Status
import org.example.project.data.commonData.Task
import org.example.project.ui.theme.LightGray
import org.example.project.ui.viewComponents.commonComponents.formatDate
import org.example.project.ui.viewComponents.taskScreenComponents.HighPriority
import org.example.project.ui.viewComponents.taskScreenComponents.LowPriority
import org.example.project.ui.viewComponents.taskScreenComponents.MediumPriority
import org.example.project.ui.viewComponents.taskScreenComponents.ToDoStatus

@Serializable
data class DetailTaskScreenArgs(val taskID: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTaskScreen(task: Task, navController: NavController) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Назад", color = MaterialTheme.colorScheme.primary) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Вернуться назад",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = LightGray
                )
            }
        },
        bottomBar = {
            Column {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = { /* TODO: Implement edit */ },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Редактировать")
                    }
                    androidx.compose.material3.OutlinedButton(
                        onClick = { /* TODO: Implement delete */ },
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Удалить")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(task.title, style = MaterialTheme.typography.displayLarge)

            Spacer(modifier = Modifier.height(30.dp))

            Text("Детали задачи", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(15.dp))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    "Приоритет: ",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Row(modifier = Modifier.weight(1f)) {
                    when (task.priority) {
                        Priority.LOW -> LowPriority()
                        Priority.MEDIUM -> MediumPriority()
                        Priority.HIGH -> HighPriority()
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    "Статус: ",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Row(modifier = Modifier.weight(1f)) {
                    when (task.status) {
                        Status.TODO -> ToDoStatus()
                        Status.IN_PROGRESS -> ToDoStatus()
                        Status.DONE -> ToDoStatus()
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    "Группа: ",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    task.group,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    "Исполнитель: ",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    task.assignee,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    "Дедлайн (увед): ",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    formatDate(task.dueDate),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    "Геометка (увед): ",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    task.geotag,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(30.dp))

            Text("Описание", style = MaterialTheme.typography.headlineLarge)

            Spacer(Modifier.height(15.dp))

            Text(task.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(30.dp))

            Text("Комментарии", style = MaterialTheme.typography.headlineLarge)
        }
    }
}