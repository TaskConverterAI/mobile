package org.example.project.android.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking
import org.example.project.AppDependencies
import org.example.project.createDataStore
import org.example.project.android.location.AndroidLocationProvider
import org.example.project.data.geo.ReminderEvaluator
import org.example.project.data.geo.ReminderSettings
import org.example.project.data.geo.ReminderNote
import org.example.project.android.AppStateTracker

class GeoReminderWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {
        val container = AppDependencies.container
        val uid = runBlocking { container.authRepository.decode()?.first ?: 0L }
        val notes = runBlocking { container.noteRepository.getAllNotes(userId = uid) ?: emptyList() }

        val appCtx = applicationContext
        val locationProvider = AndroidLocationProvider(appCtx)
        val lastPushStore = LastPushDataStore(createDataStore())
        val evaluator = ReminderEvaluator(
            settings = ReminderSettings(
                distanceThresholdMeters = 300.0,
                minIntervalBetweenPushMillis = 2 * 60 * 60 * 1000L
            ),
            lastPushStore = lastPushStore,
            locationProvider = locationProvider
        )

        val notif = NotificationHelper(appCtx)
        notif.ensureChannel()

        val isForeground = AppStateTracker.isForeground.value

        notes.forEach { note ->
            val rn = ReminderNote(
                id = note.id,
                title = note.title,
                geotag = note.geotag?.let { "${it.latitude},${it.longitude}" },
                reminderEnabled = note.reminderEnabled
            )
            val res = runBlocking { evaluator.evaluate(rn) }
            if (res.shouldPush) {
                if (isForeground) {
                    InAppNotifier.push(InAppNotifier.Message(note.id, note.title, "Вы рядом с местом заметки"))
                } else {
                    notif.showSystemNotification(note.id, note.title, "Вы рядом с местом заметки")
                }
                runBlocking { evaluator.onPushed(note.id) }
            }
        }
        return Result.success()
    }
}
