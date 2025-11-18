package org.example.project.ui.screens.notesScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.example.project.ui.TaskConvertAIViewModel
import androidx.compose.runtime.collectAsState

import org.example.project.ui.viewComponents.commonComponents.DividerWithText

@Composable
fun NoteCreateDialog(
    viewModel: TaskConvertAIViewModel,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val selectedFile = viewModel.selectedFileUri.collectAsState()
    val filePicker = createFilePicker {uri -> viewModel.onFileSelected(uri)}

    if (selectedFile.value != null) {
        viewModel.clearFile()
        onConfirm("tasks")
    }

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
                text = "Создай заметку",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = {
                    filePicker.launch()
                          },
                modifier = Modifier.fillMaxWidth().padding(20.dp),
            ) {
                Text("Из медиафайла", style = MaterialTheme.typography.bodyMedium)
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

