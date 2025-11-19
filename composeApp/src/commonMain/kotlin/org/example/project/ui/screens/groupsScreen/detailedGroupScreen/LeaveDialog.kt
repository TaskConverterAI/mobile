package org.example.project.ui.screens.groupsScreen.detailedGroupScreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.example.project.ui.theme.TaskConvertAIAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LeaveGroupDialog(
    groupName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
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
                text = "Вы точно хотите покинуть группу \"${groupName}\" ?",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = { onConfirm() },
                modifier = Modifier.fillMaxWidth().padding(20.dp),
            ) {
                Text("Да, покинуть", style = MaterialTheme.typography.bodyMedium)
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaveGroupDialogPreview() {
    TaskConvertAIAppTheme {
        LeaveGroupDialog("TaskConvertAI", {},{})
    }
}
