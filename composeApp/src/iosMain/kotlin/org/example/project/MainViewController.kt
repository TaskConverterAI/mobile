package org.example.project

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController

import org.example.project.ui.TaskConvertAIApp
import org.example.project.ui.theme.TaskConvertAIAppTheme

fun MainViewController() = ComposeUIViewController {
    initializeIOS()

    TaskConvertAIAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            TaskConvertAIApp()
        }
    }
}