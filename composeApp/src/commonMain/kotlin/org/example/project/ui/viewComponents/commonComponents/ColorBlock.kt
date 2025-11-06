package org.example.project.ui.viewComponents.commonComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.data.commonData.Group

import org.example.project.data.commonData.Note
import org.example.project.data.commonData.Task


enum class BlockType {
    SIMPLE_NOTE,
    ADVANCED_NOTE,
    SIMPLE_TASK,
    ADVANCED_TASK,
    GROUP
}

@Composable
fun SimpleNoteBlock(
    note: Note,
    backgroundColor: Color? = null,
    modifier: Modifier = Modifier,
    onTitleClick: () -> Unit = {},
    onContentClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = note.title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable { onTitleClick() }
        )

        if (note.content.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = note.contentMaxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { onContentClick() }
            )
        }
    }
}

@Composable
fun AdvancedNoteBlock(
    note: Note,
    backgroundColor: Color? = null,
    modifier: Modifier = Modifier
) {

}

@Composable
fun SimpleTaskBlock(
    task: Task,
    backgroundColor: Color? = null,
    modifier: Modifier = Modifier,
    onTitleClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = task.title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable { onTitleClick() }
        )
    }
}

@Composable
fun AdvancedTaskBlock(
    task: Task,
    backgroundColor: Color? = null,
    modifier: Modifier = Modifier
) {

}

@Composable
fun GroupBlock(
    group: Group,
    backgroundColor: Color? = null,
    modifier: Modifier = Modifier
) {

}


@Composable
fun ColorBlock(
    blockType: BlockType,
    task: Task? = null,
    note: Note? = null,
    group: Group? = null,
    backgroundColor: Color,
    modifier: Modifier = Modifier
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
                BlockType.ADVANCED_NOTE -> AdvancedNoteBlock(note = note!!)

                BlockType.SIMPLE_TASK -> SimpleTaskBlock(
                    task = task!!,
                    onTitleClick = {
                        dialogTitle = task.title
                        showTitleDialog = true
                    }
                )
                BlockType.ADVANCED_TASK -> AdvancedTaskBlock(task = task!!)

                BlockType.GROUP -> GroupBlock(group = group!!)
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

