package org.example.project

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yandex.mapkit.MapKitFactory
import org.example.project.data.analyzer.initAnalyzerRepository
import org.example.project.data.auth.initAuthRepository

private const val AUTH_PREFERENCES = "auth_preferences"
private const val MAPKIT_META_NAME = "YANDEX_MAPKIT_API_KEY"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = AUTH_PREFERENCES
)

// Store context for DataStore creation
private lateinit var applicationContext: Context

fun initializeAndroid(context: Context) {
    applicationContext = context.applicationContext

    // Initialize MapKit with API key from manifest meta-data if present
    try {
        val appInfo: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, android.content.pm.PackageManager.GET_META_DATA)
        val metaData: Bundle? = appInfo.metaData
        val apiKey: String? = metaData?.getString(MAPKIT_META_NAME)
        if (!apiKey.isNullOrBlank()) {
            MapKitFactory.setApiKey(apiKey)
        }
        MapKitFactory.initialize(applicationContext)
    } catch (_: Throwable) {
        // Ignore, MapKit may already be initialized or meta-data missing
    }

    initAnalyzerRepository(applicationContext)
    initAuthRepository(applicationContext)
    AppDependencies.initialize()
}

actual fun createDataStore(): DataStore<Preferences> {
    return applicationContext.dataStore
}
