package org.example.project.ui.screens.tasksScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController

import org.example.project.data.commonData.Note
import org.example.project.ui.viewComponents.commonComponents.DividerWithText

@Composable
fun TaskCreateDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    notes: List<Note>,
    navController: NavController
) {
    var showChooseNoteDialog by remember { mutableStateOf(false) }

    if (!showChooseNoteDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Создай задачу",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = { showChooseNoteDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                ) {
                    Text("На основе заметки", style = MaterialTheme.typography.bodyMedium)
                }

                DividerWithText(text = "или")

                OutlinedButton(
                    onClick = { onConfirm("check_analysis_screen") },
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text("Вручную", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    if (showChooseNoteDialog) {
        ChooseNoteDialog(
            notes = notes,
            onDismiss = {
                onDismiss()
            },
            navController = navController
        )
    }
}

