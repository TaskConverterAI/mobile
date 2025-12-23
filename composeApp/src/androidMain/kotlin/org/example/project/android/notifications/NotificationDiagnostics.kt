package org.example.project.android.notifications

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import co.touchlab.kermit.Logger

/**
 * Утилитный класс для диагностики и отладки системы уведомлений
 */
object NotificationDiagnostics {
    private val logger = Logger.withTag("NotificationDiagnostics")

    /**
     * Получает подробный отчет о состоянии разрешений и настроек уведомлений
     */
    fun getDiagnosticsReport(context: Context): String {
        val report = StringBuilder()
        report.appendLine("=== ДИАГНОСТИКА УВЕДОМЛЕНИЙ ===")
        report.appendLine()

        // Информация о системе
        report.appendLine("Информация о системе:")
        report.appendLine("- Android версия: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        report.appendLine("- Производитель: ${Build.MANUFACTURER}")
        report.appendLine("- Модель: ${Build.MODEL}")
        report.appendLine()

        // Проверка разрешений на уведомления
        report.appendLine("Разрешения на уведомления:")
        val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        report.appendLine("- Уведомления разрешены: $notificationsEnabled")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPostNotifications = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            report.appendLine("- POST_NOTIFICATIONS разрешение: $hasPostNotifications")
        } else {
            report.appendLine("- POST_NOTIFICATIONS разрешение: не требуется (Android < 13)")
        }
        report.appendLine()

        // Проверка алармов
        report.appendLine("Разрешения на алармы:")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val canScheduleExact = try {
                alarmManager.canScheduleExactAlarms()
            } catch (e: Exception) {
                logger.e(e) { "Ошибка при проверке разрешения на алармы: ${e.message}" }
                false
            }
            report.appendLine("- Точные алармы разрешены: $canScheduleExact")
            report.appendLine("- SCHEDULE_EXACT_ALARM требуется: да (Android 12+)")
        } else {
            report.appendLine("- Точные алармы разрешены: да (Android < 12)")
            report.appendLine("- SCHEDULE_EXACT_ALARM требуется: нет")
        }
        report.appendLine()

        // Проверка местоположения
        report.appendLine("Разрешения на местоположение:")
        val hasFineLocation = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        report.appendLine("- ACCESS_FINE_LOCATION: $hasFineLocation")
        report.appendLine("- ACCESS_COARSE_LOCATION: $hasCoarseLocation")
        report.appendLine("- Геонапоминания доступны: ${hasFineLocation || hasCoarseLocation}")
        report.appendLine()

        // Канал уведомлений
        report.appendLine("Канал уведомлений:")
        val notificationManager = androidx.core.app.NotificationManagerCompat.from(context)
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.getNotificationChannel(NotificationHelper.CHANNEL_ID)
        } else {
            null
        }

        if (channel != null) {
            report.appendLine("- Канал '${NotificationHelper.CHANNEL_ID}' существует: да")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                report.appendLine("- Важность канала: ${channel.importance}")
            }
        } else {
            report.appendLine("- Канал '${NotificationHelper.CHANNEL_ID}' существует: нет")
        }
        report.appendLine()

        // Рекомендации
        report.appendLine("Рекомендации для решения проблем:")
        if (!notificationsEnabled) {
            report.appendLine("❌ Включите уведомления для приложения в настройках Android")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                report.appendLine("❌ Разрешите точные алармы в настройках Android > Приложения > ${getAppName(context)} > Дополнительные разрешения")
            }
        }

        if (!hasFineLocation && !hasCoarseLocation) {
            report.appendLine("❌ Разрешите доступ к местоположению для геонапоминаний")
        }

        if (notificationsEnabled && (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms())) {
            report.appendLine("✅ Все основные разрешения предоставлены")
        }

        return report.toString()
    }

    /**
     * Логирует краткий статус разрешений
     */
    fun logPermissionStatus(context: Context) {
        logger.d { "=== СТАТУС РАЗРЕШЕНИЙ ===" }

        val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        logger.d { "Уведомления: $notificationsEnabled" }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val canScheduleExact = alarmManager.canScheduleExactAlarms()
            logger.d { "Точные алармы: $canScheduleExact" }
        } else {
            logger.d { "Точные алармы: доступны (Android < 12)" }
        }

        val hasLocation = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        logger.d { "Местоположение: $hasLocation" }
        logger.d { "=========================" }
    }

    private fun getAppName(context: Context): String {
        return try {
            val applicationInfo = context.applicationInfo
            val stringId = applicationInfo.labelRes
            if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
        } catch (e: Exception) {
            context.packageName
        }
    }
}
