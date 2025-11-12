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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CheckAnalysisScreen(navController: NavController) {
    // Состояние для задач (пример данных)
    var tasks by remember {
        mutableStateOf(
            listOf(
                "Купить продукты в магазине" to true,
                "Позвонить врачу и записаться на прием" to true,
                "Сделать домашнее задание по математике" to false,
                "Отправить отчет руководителю" to true,
                "Забрать посылку из почтового отделения" to false
            )
        )
    }

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
                title = "Wasd",
                content = "Wasd was wasd",
                geotag = "office",
                group = "work",
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
                tasks.forEachIndexed { index, (taskTitle, isEnabled) ->
                    TaskChoosingItem(
                        task = Task(
                            title = taskTitle,
                            description = "empty",
                            comments = emptyList(),
                            group = "standart",
                            assignee = "me",
                            dueDate = 0,
                            geotag = "empty",
                            priority = Priority.HIGH,
                            status = Status.IN_PROGRESS
                        ),
                        isEnabled = isEnabled,
                        onEnabledChange = { newValue ->
                            tasks = tasks.toMutableList().apply {
                                this[index] = taskTitle to newValue
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
            //end of scrollable content

            Button(
                onClick = {
                    // ToDo: Save note and tasks
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
