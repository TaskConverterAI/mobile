package org.example.project.android.notifications

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import co.touchlab.kermit.Logger

/**
 * Утилиты для работы с разрешениями на точные алармы
 */
object AlarmPermissionHelper {
    private val logger = Logger.withTag("AlarmPermissionHelper")

    /**
     * Проверяет, есть ли разрешение на планирование точных алармов
     */
    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true // До Android 12 разрешение не требуется
        }
    }

    /**
     * Открывает настройки для предоставления разрешения на точные алармы
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun requestExactAlarmPermission(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            logger.d { "Открыты настройки для запроса разрешения на точные алармы" }
        } catch (e: Exception) {
            logger.e(e) { "Ошибка при открытии настроек алармов: ${e.message}" }
            // Fallback - открываем общие настройки приложения
            try {
                val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
            } catch (e2: Exception) {
                logger.e(e2) { "Ошибка при открытии fallback настроек: ${e2.message}" }
            }
        }
    }

    /**
     * Показывает информацию о необходимости разрешения на точные алармы
     */
    fun getExactAlarmPermissionInfo(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            "Для точных временных напоминаний требуется разрешение 'Точные алармы' в настройках приложения."
        } else {
            "Точные алармы поддерживаются на вашей версии Android."
        }
    }
}
