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

import org.example.project.ui.viewComponents.commonComponents.DividerWithText

@Composable
fun NoteCreateDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
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
                onClick = { onConfirm("start_transcribing_screen") },
                modifier = Modifier.fillMaxWidth().padding(20.dp),
            ) {
                Text("Из медиафайла", style = MaterialTheme.typography.bodyMedium)
            }

            DividerWithText(text = "или")

            OutlinedButton(
                onClick = {
                    // Navigate to DetailNoteScreen with null noteID to create a new note
                    onConfirm("create_manual_note")
                },
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
