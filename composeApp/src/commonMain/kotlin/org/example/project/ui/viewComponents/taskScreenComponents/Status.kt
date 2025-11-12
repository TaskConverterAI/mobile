package org.example.project.ui.viewComponents.taskScreenComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToDoStatus() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(
                red = MaterialTheme.colorScheme.primary.red * 0.2f + 0.8f,
                green = MaterialTheme.colorScheme.primary.green * 0.2f + 0.8f,
                blue = MaterialTheme.colorScheme.primary.blue * 0.2f + 0.8f
            )
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text("ToDo",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Composable
fun InProgressStatus() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(
                red = MaterialTheme.colorScheme.primary.red * 0.2f + 0.8f,
                green = MaterialTheme.colorScheme.primary.green * 0.2f + 0.8f,
                blue = MaterialTheme.colorScheme.primary.blue * 0.2f + 0.8f
            )
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text("In Progress",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Composable
fun DoneStatus() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(
                red = MaterialTheme.colorScheme.primary.red * 0.2f + 0.8f,
                green = MaterialTheme.colorScheme.primary.green * 0.2f + 0.8f,
                blue = MaterialTheme.colorScheme.primary.blue * 0.2f + 0.8f
            )
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text("Done",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}
