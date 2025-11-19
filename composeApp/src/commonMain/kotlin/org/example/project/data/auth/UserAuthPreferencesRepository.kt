package org.example.project.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserAuthPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val accessToken: Flow<String> = dataStore.data
        .catch { exception ->
            // Log error and emit empty preferences
            println("Error reading access token preferences: ${exception.message}")
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[ACCESS_TOKEN] ?: ""
        }

    val refreshToken: Flow<String> = dataStore.data
        .catch { exception ->
            // Log error and emit empty preferences
            println("Error reading refresh token preferences: ${exception.message}")
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[REFRESH_TOKEN] ?: ""
        }

    val userId: Flow<String> = dataStore.data
        .catch { exception ->
            // Log error and emit empty preferences
            println("Error reading refresh token preferences: ${exception.message}")
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[USER_ID] ?: ""
        }


    suspend fun saveAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")

        val USER_ID = stringPreferencesKey("user_id")

        const val TAG = "UserAuthPreferencesRepo"
    }
}
