package org.example.project.ui.screens.notesScreen

import androidx.compose.runtime.Composable

@Composable
actual fun MapPickerScreen(
    onPicked: (lat: Double, lon: Double, name: String?) -> Unit,
    onBack: () -> Unit
) {
    // TODO: Реализовать экран выбора точки на карте для iOS.
    onBack()
}
