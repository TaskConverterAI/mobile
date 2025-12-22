package org.example.project.ui.screens.notesScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.touchlab.kermit.Logger

import org.example.project.ui.screens.notesScreen.conditionScreens.EmptyMainScreen
import org.example.project.ui.screens.notesScreen.conditionScreens.MainScreenWithNotes
import org.example.project.ui.viewmodels.NotesViewModel
import org.example.project.ui.screens.statusToast.StatusToast
import org.example.project.ui.screens.statusToast.ToastDuration

@Composable
fun NotesScreen(navController: NavController) {
    val logger = Logger.withTag("NotesScreen")
    // Получаем экземпляр ViewModel
    val viewModel: NotesViewModel = viewModel(factory = NotesViewModel.Factory)

    // Инициируем загрузку заметок при первом показе экрана
    LaunchedEffect(viewModel) {
        logger.i { "NotesScreen: LaunchedEffect loadNotes" }
        viewModel.loadNotes()
    }

    // Собираем состояния из ViewModel
    val notes by viewModel.notes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    // Загружаем заметки только один раз
    LaunchedEffect(Unit) {
        viewModel.loadNotes()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Показываем индикатор загрузки
            isLoading -> {
                logger.i { "NotesScreen: show Loading" }
                CircularProgressIndicator()
            }

            // Показываем ошибку, если есть
            error != null -> {
                logger.e { "NotesScreen: show Error ${error}" }
                Text(text = "Ошибка: $error")
            }

            // Показываем пустой экран, если нет заметок
            notes.isEmpty() -> {
                logger.i { "NotesScreen: show Empty, count=${notes.size}" }
                EmptyMainScreen()
            }

            // Показываем экран с заметками
            else -> {
                logger.i { "NotesScreen: show Main, count=${notes.size}" }
                MainScreenWithNotes(navController, viewModel)
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
