package org.example.project.ui.screens.notesScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import org.example.project.ui.screens.notesScreen.conditionScreens.MainScreenWithNotes

@Composable
fun NotesScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // ToDo: switch between different states
        MainScreenWithNotes()
    }
}
