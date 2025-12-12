package org.example.project.android.notifications

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.first
import org.example.project.data.geo.LastPushStore

@OptIn(kotlin.time.ExperimentalTime::class)
class LastPushDataStore(private val dataStore: DataStore<Preferences>) : LastPushStore {
    private fun key(noteId: Long) = longPreferencesKey("last_push_note_$noteId")

    override suspend fun getLastPushAtMillis(noteId: Long): Long? {
        val prefs = dataStore.data.first()
        return prefs[key(noteId)]
    }

    override suspend fun setLastPushAtMillis(noteId: Long, atMillis: Long) {
        dataStore.edit { prefs ->
            prefs[key(noteId)] = atMillis
        }
    }
}
