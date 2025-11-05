package org.example.project

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import okio.Path.Companion.toPath

private const val AUTH_PREFERENCES = "auth_preferences"

@OptIn(ExperimentalForeignApi::class)
private fun getDocumentsDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    return requireNotNull(documentDirectory?.path)
}

fun initializeIOS() {
    AppDependencies.initialize()
}

actual fun createDataStore(): DataStore<Preferences> {
    val documentsDir = getDocumentsDirectory()
    val dataStoreFile = "$documentsDir/$AUTH_PREFERENCES.preferences_pb"

    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { dataStoreFile.toPath() }
    )
}
