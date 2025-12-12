package org.example.project.android.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object GeoReminderScheduler {
    private const val UNIQUE_WORK_NAME = "geo_reminder_worker"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<GeoReminderWorker>(30, TimeUnit.MINUTES)
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}

