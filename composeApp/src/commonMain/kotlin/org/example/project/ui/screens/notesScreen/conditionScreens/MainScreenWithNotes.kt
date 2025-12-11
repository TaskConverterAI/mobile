package org.example.project.ui.screens.notesScreen.conditionScreens

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
import org.example.project.data.auth.AuthRepository

import org.example.project.data.commonData.Note
import org.example.project.ui.viewComponents.commonComponents.BlockType
import org.example.project.ui.viewComponents.commonComponents.ColorBlock
import org.example.project.ui.viewComponents.commonComponents.FilterSelector
import org.example.project.ui.viewComponents.commonComponents.NotifyItem
import org.example.project.ui.viewmodels.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithNotes(navController: NavController, viewModel: NotesViewModel) {
    var selectedFilter by remember { mutableStateOf("Все группы") }
    val filterOptions = listOf("Все группы")

    var showBottomSheet by remember { mutableStateOf(false) }

    // Получаем заметки из viewModel
    viewModel.loadNotes()
    val notes by viewModel.notes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp)
    ) {
        Text(
            text = "Заметки",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 5.dp, start = 10.dp).scale(1.1F)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Фильтр:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            )

            FilterSelector(
                selectedFilter = selectedFilter,
                filterOptions = filterOptions,
                onFilterSelected = { selectedFilter = it },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { showBottomSheet = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Test Notify")
        }

        // Разделяем заметки на две колонки для отображения

        val leftColumnNotes = mutableListOf<Note>()
        val rightColumnNotes = mutableListOf<Note>()

        notes.forEachIndexed { index, note ->
            if (index % 2 == 0) {
                leftColumnNotes.add(note)
            } else {
                rightColumnNotes.add(note)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                leftColumnNotes.forEach { note ->
                    ColorBlock(blockType = BlockType.ADVANCED_NOTE, note = note, backgroundColor = note.color, navController = navController)
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rightColumnNotes.forEach { note ->
                    ColorBlock(blockType = BlockType.ADVANCED_NOTE, note = note, backgroundColor = note.color, navController = navController)
                }
            }
        }
    }

    if (showBottomSheet) {
        NotifyItem(
            objectName = "Заметка успешно создана",
            objectDescription = "Извлечённые задачи можешь найти в разделе задач",
            onDismiss = { showBottomSheet = false }
        )
    }
}

