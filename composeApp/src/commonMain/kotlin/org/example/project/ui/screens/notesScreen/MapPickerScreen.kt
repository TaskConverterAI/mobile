package org.example.project.ui.screens.notesScreen

import androidx.compose.runtime.Composable

@Composable
expect fun MapPickerScreen(
    onPicked: (lat: Double, lon: Double, name: String?, colorLong: Long?) -> Unit,
    onBack: () -> Unit
)
