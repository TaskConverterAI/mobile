package org.example.project.data.geo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
data class GeoTagPreset(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val colorValueLong: Long? = null
)

class GeoTagRepository(private val dataStore: DataStore<Preferences>) {
    private val KEY = stringPreferencesKey("geo_tag_presets_json")
    private val json = Json { ignoreUnknownKeys = true }

    fun presetsFlow(): Flow<List<GeoTagPreset>> = dataStore.data.map { prefs ->
        val raw = prefs[KEY]
        if (raw.isNullOrBlank()) emptyList()
        else runCatching { json.decodeFromString(ListSerializer(GeoTagPreset.serializer()), raw) }.getOrElse { emptyList() }
    }

    suspend fun addPreset(preset: GeoTagPreset) {
        dataStore.edit { prefs ->
            val current = prefs[KEY]
            val list = if (current.isNullOrBlank()) emptyList() else runCatching { json.decodeFromString(ListSerializer(GeoTagPreset.serializer()), current) }.getOrElse { emptyList() }
            val updated = (list.filterNot { it.name == preset.name } + preset)
            prefs[KEY] = json.encodeToString(ListSerializer(GeoTagPreset.serializer()), updated)
        }
    }
}
