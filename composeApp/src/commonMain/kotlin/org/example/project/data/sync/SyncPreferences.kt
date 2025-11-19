package org.example.project.data.sync

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Хранилище настроек синхронизации
 */
class SyncPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
    }

    /**
     * Получить timestamp последней синхронизации
     */
    suspend fun getLastSyncTimestamp(): Long {
        return dataStore.data.map { preferences ->
            preferences[LAST_SYNC_TIMESTAMP] ?: 0L
        }.first()
    }

    /**
     * Сохранить timestamp последней синхронизации
     */
    suspend fun setLastSyncTimestamp(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_SYNC_TIMESTAMP] = timestamp
        }
    }

    /**
     * Очистить данные синхронизации
     */
    suspend fun clearSyncData() {
        dataStore.edit { preferences ->
            preferences.remove(LAST_SYNC_TIMESTAMP)
        }
    }
}

