package org.example.project.ui.screens.notesScreen.creatingNoteScreens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

import org.example.project.data.commonData.Note
import org.example.project.data.commonData.Priority
import org.example.project.data.commonData.Status
import org.example.project.data.commonData.Task
import org.example.project.ui.theme.LightGray
import org.example.project.ui.theme.PrimaryBase
import org.example.project.ui.viewComponents.commonComponents.BlockType
import org.example.project.ui.viewComponents.commonComponents.ColorBlock
import org.example.project.ui.viewComponents.noteScreenComponents.TaskChoosingItem
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState

@Serializable
data class CheckAnalysisScreenArgs(val jobId: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CheckAnalysisScreen(navController: NavController, viewModel: CheckAnalysisViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val uiData by viewModel.uiData.collectAsState()
    var noteVisible by remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }
    var pendingError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pendingError) {
        pendingError?.let { msg ->
            snackbarHostState.showSnackbar(message = msg, duration = SnackbarDuration.Short)
            pendingError = null
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Назад", color = MaterialTheme.colorScheme.surface)
                        }
                    },
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Text(
                "Оцени результаты",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(20.dp).scale(1.1F)
            )

            Text(
                "Можешь отредактировать заметку и задачи к ней, " +
                        "оставив при этом только нужные",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (noteVisible) {
                val testNote = Note(
                    title = uiData.noteTitle,
                    content = uiData.summary,
                    authorId = 1,
                    geotag = null,
                    groupId = null,
                    comments = emptyList(),
                    color = PrimaryBase,
                    contentMaxLines = 5,
                    creationDate = 0
                )

                val dismissState = rememberSwipeToDismissBoxState()

                // Отслеживаем состояние смахивания
                LaunchedEffect(dismissState.currentValue) {
                    if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                        noteVisible = false
                    }
                }

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = true,
                    enableDismissFromEndToStart = true,
                    backgroundContent = {
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.surface
                                SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.EndToStart ->
                                    MaterialTheme.colorScheme.errorContainer
                            }
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd)
                                Alignment.CenterStart else Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                ) {
                    ColorBlock(
                        blockType = BlockType.SIMPLE_NOTE,
                        note = testNote,
                        backgroundColor = testNote.color,
                        onTitleEdit = { newTitle ->
                            viewModel.updateNoteTitle(newTitle)
                        },
                        onContentEdit = { newContent ->
                            viewModel.updateNoteContent(newContent)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Scrollable content with LazyColumn
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(tasks) { index, taskCell ->
                    TaskChoosingItem(
                        task = Task(
                            id = 0,
                            title = taskCell.task.title,
                            description = taskCell.task.description,
                            comments = emptyList(),
                            authorId = 1,
                            groupId = null,
                            assignee = 2,
                            dueDate = null,
                            geotag = null,
                            priority = Priority.MIDDLE,
                            status = Status.UNDONE
                        ),
                        isEnabled = taskCell.isUsed,
                        onEnabledChange = { newValue ->
                            viewModel.updateTaskUsing(index, newValue)
                        },
                        onTitleEdit = { newTitle ->
                            viewModel.updateTaskTitle(index, newTitle)
                        },
                        onDescriptionEdit = { newDescription ->
                            viewModel.updateTaskDescription(index, newDescription)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            //end of scrollable content

            val saveInProgress by viewModel.saveInProgress.collectAsState()

            Button(
                onClick = {
                    viewModel.saveNoteAndTasks(
                        onSuccess = {
                            navController.popBackStack()
                        },
                        onError = { errorMessage ->
                            pendingError = errorMessage.ifBlank { "Ошибка при сохранении" }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .height(56.dp),
                enabled = !saveInProgress
            ) {
                Text(
                    if (saveInProgress) "Сохранение..." else "Сохранить",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
