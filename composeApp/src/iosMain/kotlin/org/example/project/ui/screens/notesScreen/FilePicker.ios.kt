package org.example.project.ui.screens.notesScreen

import androidx.compose.runtime.Composable

actual class FilePickerController actual constructor() {
    actual fun launch() {
    }
}

@Composable
actual fun createFilePicker(onFileSelected: (String?) -> Unit): FilePickerController {
    TODO("Not yet implemented")
}