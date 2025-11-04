package com.example.taskconvertaiapp.shared

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

import com.example.taskconvertaiapp.shared.data.AppContainer
import com.example.taskconvertaiapp.shared.data.DefaultAppContainer

// Expect function to create platform-specific DataStore
expect fun createDataStore(): DataStore<Preferences>

// Global singleton for AppContainer
object AppDependencies {
    private var _container: AppContainer? = null

    val container: AppContainer
        get() = _container ?: throw IllegalStateException("AppContainer not initialized. Call initialize() first.")

    fun initialize() {
        if (_container == null) {
            val dataStore = createDataStore()
            _container = DefaultAppContainer(dataStore)
        }
    }

    fun isInitialized(): Boolean = _container != null
}
