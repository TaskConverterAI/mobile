package com.example.taskconvertaiapp.shared.ui.screens.tasks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.example.taskconvertaiapp.shared.ui.theme.TaskConvertAIAppTheme

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
