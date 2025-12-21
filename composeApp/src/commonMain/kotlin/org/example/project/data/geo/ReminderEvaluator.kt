package org.example.project.data.geo

import kotlin.math.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Контракт провайдера текущей локации (кросс-платформенный интерфейс, реализация на Android).
 */
interface LocationProvider {
    suspend fun getLastKnownLocation(): Location?
}

/** Простая модель локации. */
data class Location(val latitude: Double, val longitude: Double)

/** Настройки напоминаний. */
data class ReminderSettings(
    val distanceThresholdMeters: Double = 300.0,
    val minIntervalBetweenPushMillis: Long = 2 * 60 * 60 * 1000L // 2 часа
)

/** Хранилище времени последнего пуша по заметке. */
interface LastPushStore {
    suspend fun getLastPushAtMillis(noteId: Long): Long?
    suspend fun setLastPushAtMillis(noteId: Long, atMillis: Long)
}

/** Данные заметки, необходимые для проверки. */
data class ReminderNote(
    val id: Long,
    val title: String,
    val geotag: String?, // ожидаем формат "lat,lon" или null
    val reminderEnabled: Boolean
)

object DistanceCalculator {
    private fun rad(deg: Double) = deg * PI / 180.0

    // Haversine distance in meters
    fun distanceMeters(a: Location, b: Location): Double {
        val R = 6371000.0
        val dLat = rad(b.latitude - a.latitude)
        val dLon = rad(b.longitude - a.longitude)
        val lat1 = rad(a.latitude)
        val lat2 = rad(b.latitude)
        val h = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(lat1) * cos(lat2)
        val c = 2 * asin(min(1.0, sqrt(h)))
        return R * c
    }

    fun parseGeotag(geotag: String?): Location? {
        if (geotag.isNullOrBlank()) return null
        val parts = geotag.split(",")
        if (parts.size != 2) return null
        val lat = parts[0].trim().toDoubleOrNull() ?: return null
        val lon = parts[1].trim().toDoubleOrNull() ?: return null
        return Location(lat, lon)
    }
}

class ReminderEvaluator(
    private val settings: ReminderSettings,
    private val lastPushStore: LastPushStore,
    private val locationProvider: LocationProvider
) {
    data class Result(val shouldPush: Boolean, val reason: String)

    @OptIn(ExperimentalTime::class)
    suspend fun evaluate(note: ReminderNote): Result {
        if (!note.reminderEnabled) return Result(false, "Reminder disabled")
        val userLoc = locationProvider.getLastKnownLocation() ?: return Result(false, "No location")
        val noteLoc = DistanceCalculator.parseGeotag(note.geotag) ?: return Result(false, "No geotag")

        val dist = DistanceCalculator.distanceMeters(userLoc, noteLoc)
        if (dist > settings.distanceThresholdMeters) return Result(false, "Too far: ${dist.toInt()}m")

        val lastMillis = lastPushStore.getLastPushAtMillis(note.id)
        val nowMillis = Clock.System.now().toEpochMilliseconds()
        if (lastMillis != null) {
            val elapsed: Long = nowMillis - lastMillis
            if (elapsed < settings.minIntervalBetweenPushMillis) {
                return Result(false, "Rate limited")
            }
        }
        // можно пушить
        return Result(true, "Within distance and not rate-limited")
    }

    @OptIn(ExperimentalTime::class)
    suspend fun onPushed(noteId: Long) {
        val nowMillis = Clock.System.now().toEpochMilliseconds()
        lastPushStore.setLastPushAtMillis(noteId, nowMillis)
    }
}
