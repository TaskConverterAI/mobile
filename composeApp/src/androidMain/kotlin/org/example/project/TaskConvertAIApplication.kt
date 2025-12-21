package org.example.project

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import org.example.project.data.database.initDatabase

class TaskConvertAIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDatabase(this)
    }
}

