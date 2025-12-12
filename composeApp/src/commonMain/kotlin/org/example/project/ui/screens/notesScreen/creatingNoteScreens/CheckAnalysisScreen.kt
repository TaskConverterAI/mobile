package org.example.project.ui.screens.notesScreen.creatingNoteScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@Serializable
data class CheckAnalysisScreenArgs(val jobId: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CheckAnalysisScreen(navController: NavController, viewModel: CheckAnalysisViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val uiData by viewModel.uiData.collectAsState()

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
        }
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

            val testNote = Note(
                title = "Заметка",
                content = uiData.summary,
                authorId = 1,
                geotag = null,
                groupId = null,
                comments = emptyList(),
                color = PrimaryBase,
                contentMaxLines = 5,
                creationDate = 0
            )

            ColorBlock(blockType = BlockType.SIMPLE_NOTE,
                note = testNote,
                backgroundColor = testNote.color
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                tasks.forEachIndexed { index, taskCell ->
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
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
            //end of scrollable content

            Button(
                onClick = {
                    // ToDo: сохранить заметку и выбранные задачи
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .height(56.dp),
                enabled = true
            ) {
                Text(
                    "Сохранить",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
