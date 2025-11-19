package org.example.project

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.example.project.data.analyzer.initAnalyzerRepository

private const val AUTH_PREFERENCES = "auth_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = AUTH_PREFERENCES
)

// Store context for DataStore creation
private lateinit var applicationContext: Context

fun initializeAndroid(context: Context) {
    applicationContext = context.applicationContext
    initAnalyzerRepository(applicationContext)
    AppDependencies.initialize()
}

actual fun createDataStore(): DataStore<Preferences> {
    return applicationContext.dataStore
}
