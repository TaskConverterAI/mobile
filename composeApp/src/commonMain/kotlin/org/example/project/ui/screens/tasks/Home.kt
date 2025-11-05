package org.example.project.ui.screens.tasks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import org.example.project.ui.theme.TaskConvertAIAppTheme

@Composable
fun HomeScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

    }
}

@Composable
private fun BirthdayCardPreview() {
    TaskConvertAIAppTheme {
        HomeScreen()
    }
}
