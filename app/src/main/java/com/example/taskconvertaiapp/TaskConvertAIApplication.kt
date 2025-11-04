package com.example.taskconvertaiapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.taskconvertaiapp.data.AppContainer
import com.example.taskconvertaiapp.data.DefaultAppContainer


private const val AUTH_PREFERENCES = "auth_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = AUTH_PREFERENCES
)
class TaskConvertAIApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(dataStore)
    }
}