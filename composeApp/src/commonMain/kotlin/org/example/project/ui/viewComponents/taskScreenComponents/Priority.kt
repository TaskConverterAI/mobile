package org.example.project.ui.viewComponents.taskScreenComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LowPriority() {
    val greenColor = Color(0xFF4CAF50)
    Card (
        colors = CardDefaults.cardColors(
            containerColor = greenColor.copy(
                red = greenColor.red * 0.2f + 0.8f,
                green = greenColor.green * 0.2f + 0.8f,
                blue = greenColor.blue * 0.2f + 0.8f
            )
        ),
        border = BorderStroke(1.dp, greenColor)
    ) {
        Text("Low",
            color = greenColor,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Composable
fun MediumPriority() {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFA500).copy(
                red = Color(0xFFFFA500).red * 0.2f + 0.8f,
                green = Color(0xFFFFA500).green * 0.2f + 0.8f,
                blue = Color(0xFFFFA500).blue * 0.2f + 0.8f
            )
        ),
        border = BorderStroke(1.dp, Color(0xFFFFA500))
    ) {
        Text("Medium",
            color = Color(0xFFFFA500),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Composable
fun HighPriority() {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(
                red = Color.Red.red * 0.2f + 0.8f,
                green = Color.Red.green * 0.2f + 0.8f,
                blue = Color.Red.blue * 0.2f + 0.8f
            )
        ),
        border = BorderStroke(1.dp, Color.Red)
    ) {
        Text("High",
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}
