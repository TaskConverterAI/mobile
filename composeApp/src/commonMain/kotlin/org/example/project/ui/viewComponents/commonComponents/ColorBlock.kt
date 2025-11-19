package org.example.project.ui.viewComponents.commonComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.commonData.Group

import org.example.project.data.commonData.Note
import org.example.project.data.commonData.Priority
import org.example.project.data.commonData.Status
import org.example.project.data.commonData.Task
import org.example.project.model.AnalysisJob
import org.example.project.model.JobType
import org.example.project.ui.screens.groupsScreen.detailedGroupScreen.DetailGroupScreenArgs
import org.example.project.ui.screens.notesScreen.DetailNoteScreenArgs
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.CheckAnalysisScreenArgs
import org.example.project.ui.screens.notesScreen.creatingNoteScreens.CheckTranscribingScreenArgs
import org.example.project.ui.screens.tasksScreen.DetailTaskScreenArgs
import org.example.project.ui.viewComponents.taskScreenComponents.DoneStatus
import org.example.project.ui.viewComponents.taskScreenComponents.HighPriority
import org.example.project.ui.viewComponents.taskScreenComponents.InProgressStatus
import org.example.project.ui.viewComponents.taskScreenComponents.LowPriority
import org.example.project.ui.viewComponents.taskScreenComponents.MediumPriority
import org.example.project.ui.viewComponents.taskScreenComponents.ToDoStatus

enum class BlockType {
    SIMPLE_NOTE,
    ADVANCED_NOTE,
    SIMPLE_TASK,
    ADVANCED_TASK,
    GROUP,
    JOB
}

@OptIn(kotlin.time.ExperimentalTime::class)
fun formatDate(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val day = localDateTime.day.toString().padStart(2, '0')
    val month = localDateTime.month.number.toString().padStart(2, '0')
    val year = localDateTime.year
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')

    return "$day.$month.$year $hour:$minute"
}

@Composable
private fun SimpleNoteBlock(
    note: Note,
    modifier: Modifier = Modifier,
    onTitleClick: () -> Unit = {},
    onContentClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = note.title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier.clickable { onTitleClick() }
        )

        if (note.content.isNotEmpty()) {
            Spacer(modifier = modifier.height(8.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = note.contentMaxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = modifier.clickable { onContentClick() }
            )
        }
    }
}

@Composable
private fun AdvancedNoteBlock(
    note: Note,
    modifier: Modifier = Modifier,
    onNoteClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onNoteClick() }
    ) {
        Text(
            text = note.title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (note.content.isNotEmpty()) {
            Spacer(modifier = modifier.height(8.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = note.contentMaxLines,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = modifier.height(8.dp))

        Text(
            text = formatDate(note.creationDate),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SimpleTaskBlock(
    task: Task,
    modifier: Modifier = Modifier,
    onTitleClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = task.title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier.clickable { onTitleClick() }
        )
    }
}

@Composable
private fun AdvancedTaskBlock(
    task: Task,
    modifier: Modifier = Modifier,
    onTaskClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onTaskClick() }
    ) {
        Text(
            text = task.title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = modifier.height(8.dp))

        Row {
            when (task.priority) {
                Priority.LOW -> {
                    LowPriority()
                }

                Priority.MEDIUM -> {
                    MediumPriority()
                }

                Priority.HIGH -> {
                    HighPriority()
                }
            }

            Spacer(modifier = modifier.width(8.dp))

            when (task.status) {
                Status.TODO -> {
                    ToDoStatus()
                }

                Status.IN_PROGRESS -> {
                    InProgressStatus()
                }

                Status.DONE -> {
                    DoneStatus()
                }
            }
        }
    }
}

@Composable
private fun GroupBlock(
    group: Group,
    onGroupClick: () -> Unit = {},
    backgroundColor: Color? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onGroupClick() }
    ) {
        Text(
            text = group.name,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = group.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "Members",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = group.members.size.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "Notes",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = group.memberCount.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun JobBlock(
    job: AnalysisJob,
    backgroundColor: Color? = null,
    modifier: Modifier = Modifier,
    onToJobClick: () -> Unit = {},
    onCloseErrorClick: () -> Unit = {}
) {
    val isInProgress = job.status == org.example.project.model.Status.PENDING ||
            job.status == org.example.project.model.Status.RUNNING
    val progress = when (job.status) {
        org.example.project.model.Status.PENDING -> 0.25f
        org.example.project.model.Status.RUNNING -> 0.7f
        else -> 0f
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor ?: MaterialTheme.colorScheme.surface,
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Задача #${job.jobId}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isInProgress) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (job.status) {
                org.example.project.model.Status.SUCCEEDED -> {
                    StatusChip(
                        text = "Done",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                org.example.project.model.Status.FAILED -> {
                    StatusChip(
                        text = "Fail",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {}
            }

            if (job.status == org.example.project.model.Status.SUCCEEDED) {
                TextButton(
                    onClick = { onToJobClick() },
                    colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("к задаче")
                }
            }

            if (job.status == org.example.project.model.Status.FAILED) {
                TextButton(
                    onClick = { onCloseErrorClick() },
                    colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("закрыть")
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = color,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}


@Composable
fun ColorBlock(
    blockType: BlockType,
    task: Task? = null,
    note: Note? = null,
    group: Group? = null,
    job: AnalysisJob? = null,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    onCloseErrorClick: () -> Unit = {}
) {
    var showTitleDialog by remember { mutableStateOf(false) }
    var showContentDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogContent by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            )
        ) {}

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = lerp(
                    backgroundColor,
                    Color.White,
                    0.85f
                )
            )
        ) {
            when (blockType) {
                BlockType.SIMPLE_NOTE -> SimpleNoteBlock(
                    note = note!!,
                    onTitleClick = {
                        dialogTitle = note.title
                        showTitleDialog = true
                    },
                    onContentClick = {
                        dialogContent = note.content
                        showContentDialog = true
                    }
                )

                BlockType.ADVANCED_NOTE -> AdvancedNoteBlock(
                    note = note!!,
                    onNoteClick = {
                        navController?.navigate(
                            DetailNoteScreenArgs(noteID = note.id.toInt(), isEditMode = false)
                        )
                    }
                )

                BlockType.SIMPLE_TASK -> SimpleTaskBlock(
                    task = task!!,
                    onTitleClick = {
                        dialogTitle = task.title
                        showTitleDialog = true
                    }
                )

                BlockType.ADVANCED_TASK -> AdvancedTaskBlock(
                    task = task!!,
                    onTaskClick = {
                        navController?.navigate(
                            DetailTaskScreenArgs(taskID = task.id, isEditMode = false)
                        )
                    }
                )

                BlockType.JOB -> JobBlock(
                    job = job!!,
                    onToJobClick = {
                        if (job.type == JobType.AUDIO) {
                            navController?.navigate(
                                CheckTranscribingScreenArgs(job.jobId)
                            )
                        } else {
                            navController?.navigate(
                                CheckAnalysisScreenArgs(job.jobId)
                            )
                        }
                    },
                    onCloseErrorClick = onCloseErrorClick
                )

                BlockType.GROUP -> GroupBlock(
                    group = group!!,
                    onGroupClick = {
                        navController?.navigate(
                            DetailGroupScreenArgs(group.name)
                        )
                    }
                )
            }
        }
    }

    if (showTitleDialog) {
        AlertDialog(
            onDismissRequest = { showTitleDialog = false },
            title = {
                Text(
                    text = "Полное название",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = dialogTitle)
                }
            },
            confirmButton = {
                TextButton(onClick = { showTitleDialog = false }) {
                    Text("OK")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    if (showContentDialog) {
        AlertDialog(
            onDismissRequest = { showContentDialog = false },
            title = {
                Text(
                    text = "Полное описание",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = dialogContent)
                }
            },
            confirmButton = {
                TextButton(onClick = { showContentDialog = false }) {
                    Text("OK")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

