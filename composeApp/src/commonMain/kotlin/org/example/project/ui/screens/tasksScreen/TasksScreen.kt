package org.example.project.ui.screens.tasksScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.example.project.ui.screens.tasksScreen.conditionScreens.EmptyMainScreen
import org.example.project.ui.screens.tasksScreen.conditionScreens.MainScreenWithTasks
import org.example.project.ui.viewmodels.TasksViewModel

@Composable
fun TasksScreen(navController: NavController) {

    val viewModel: TasksViewModel = viewModel(factory = TasksViewModel.Factory)

    // Собираем состояния из ViewModel
    val notes by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Показываем индикатор загрузки
            isLoading -> {
                CircularProgressIndicator()
            }

            // Показываем ошибку, если есть
            error != null -> {
                Text(text = "Ошибка: $error")
            }

            // Показываем пустой экран, если нет заметок
            notes.isEmpty() -> {
                EmptyMainScreen()
            }

            // Показываем экран с заметками
            else -> {
                MainScreenWithTasks(navController, viewModel)
            }
        }
    }
}
