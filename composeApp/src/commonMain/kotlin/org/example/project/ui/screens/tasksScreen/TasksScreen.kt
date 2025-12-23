package org.example.project.ui.screens.tasksScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.example.project.ui.screens.tasksScreen.conditionScreens.EmptyMainScreen
import org.example.project.ui.screens.tasksScreen.conditionScreens.MainScreenWithTasks
import org.example.project.ui.viewmodels.TasksViewModel
import org.example.project.ui.screens.statusToast.StatusToast
import org.example.project.ui.screens.statusToast.ToastDuration

@Composable
fun TasksScreen(
    navController: NavController,
    jobView: org.example.project.ui.screens.tasksScreen.TasksViewModel,
    viewModel: TasksViewModel
) {
    // Собираем состояния из ViewModel
    val notes by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val jobs by jobView.currentJobs.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Загружаем задачи только один раз
    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    // Показать короткий тост при ошибках
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                withDismissAction = false,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SnackbarHost(hostState = snackbarHostState)
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
                if (jobs.isNotEmpty())
                    MainScreenWithTasks(navController, jobView, viewModel)
                else
                    EmptyMainScreen()
            }

            // Показываем экран с заметками
            else -> {
                MainScreenWithTasks(navController, jobView, viewModel)
            }
        }

        // Показываем toast, если есть сообщение
        toastMessage?.let { toast ->
            StatusToast(
                type = toast.type,
                message = toast.message,
                duration = ToastDuration.SHORT,
                onDismiss = { viewModel.clearToast() }
            )
        }
    }
}
