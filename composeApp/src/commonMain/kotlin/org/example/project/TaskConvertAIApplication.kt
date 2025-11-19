package org.example.project

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

import org.example.project.data.AppContainer
import org.example.project.data.DefaultAppContainer
import org.example.project.data.database.DatabaseProvider

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
            _container = DefaultAppContainer(dataStore, DatabaseProvider.getDatabase())
        }
    }

    fun isInitialized(): Boolean = _container != null
}
