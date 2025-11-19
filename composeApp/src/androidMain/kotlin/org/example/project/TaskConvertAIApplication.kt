package org.example.project

import android.app.Application
import org.example.project.data.database.initDatabase

class TaskConvertAIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDatabase(this)
    }
}

