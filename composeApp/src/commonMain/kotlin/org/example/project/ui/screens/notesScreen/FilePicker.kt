package org.example.project.ui.screens.notesScreen

import androidx.compose.runtime.Composable


@Composable
expect fun createFilePicker(onFileSelected: (String?) -> Unit): FilePickerController


expect class FilePickerController() {
    fun launch()
}