package org.example.project

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.yandex.mapkit.MapKitFactory
import org.example.project.data.database.initDatabase

class TaskConvertAIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDatabase(this)
        // Initialize Yandex MapKit API key from manifest meta-data
        try {
            val ai: ApplicationInfo =
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val apiKey = ai.metaData?.getString("YANDEX_MAPKIT_API_KEY")
            if (!apiKey.isNullOrBlank()) {
                MapKitFactory.setApiKey(apiKey)
                MapKitFactory.initialize(this)
            }
        } catch (_: Exception) {
            // swallow and let MapKit throw if used without key
        }
    }
}

