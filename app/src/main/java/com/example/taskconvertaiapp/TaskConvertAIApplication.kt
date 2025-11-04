package com.example.taskconvertaiapp

import android.app.Application
import com.example.taskconvertaiapp.shared.initializeAndroid

class TaskConvertAIApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the shared dependencies with Android context
        initializeAndroid(this)
    }
}
