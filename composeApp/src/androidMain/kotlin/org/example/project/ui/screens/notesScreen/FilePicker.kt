package org.example.project.ui.screens.notesScreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

@Composable
actual fun createFilePicker(onFileSelected: (String?) -> Unit): FilePickerController {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            onFileSelected(uri?.toString())
        }
    )

    val controller = remember { FilePickerController() }

    LaunchedEffect(controller, launcher) {
        controller.setLaunchHandler {
            launcher.launch(arrayOf("video/*", "audio/*"))
        }
    }

    return controller
}

actual class FilePickerController actual constructor() {
    private var onLaunch: (() -> Unit)? = null
    fun setLaunchHandler(launch: () -> Unit) {
        this.onLaunch = launch
    }

    actual fun launch() {
        onLaunch?.invoke()
    }
}