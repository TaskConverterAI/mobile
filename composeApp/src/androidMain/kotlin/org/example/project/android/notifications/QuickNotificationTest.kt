package org.example.project.android.notifications

import android.content.Context
import co.touchlab.kermit.Logger
import org.example.project.AppDependencies

/**
 * Быстрые тесты уведомлений для отладки
 */
object QuickNotificationTest {
    private val logger = Logger.withTag("QuickNotificationTest")

    /**
     * Быстрый тест - отправить уведомление немедленно
     */
    fun testNow(context: Context) {
        logger.d { "QuickTest: отправка немедленного уведомления" }

        try {
            val notificationHelper = NotificationHelper(context)
            notificationHelper.ensureChannel()
            notificationHelper.showTaskNotification(
                taskId = 8888L,
                title = "🚀 БЫСТРЫЙ ТЕСТ",
                body = "Это немедленное тестовое уведомление. Если вы его видите - push работают!"
            )
            logger.d { "QuickTest: уведомление отправлено" }
        } catch (e: Exception) {
            logger.e(e) { "QuickTest: ошибка отправки уведомления - ${e.message}" }
        }
    }

    /**
     * Тест AlarmManager - запланировать уведомление через 10 секунд
     */
    fun testAlarm(context: Context) {
        logger.d { "QuickTest: планирование уведомления через 10 секунд" }

        try {
            val triggerTime = System.currentTimeMillis() + 10 * 1000L

            TimeReminderReceiver.scheduleReminder(
                context = context,
                taskId = 7777L,
                taskTitle = "🕐 ТЕСТ АЛАРМА",
                triggerTime = triggerTime,
                reminderType = "быстрый тест через 10 сек"
            )

            logger.d { "QuickTest: алarm запланирован на $triggerTime" }

            // Также отправляем немедленное уведомление для подтверждения
            testNow(context)

        } catch (e: Exception) {
            logger.e(e) { "QuickTest: ошибка планирования алама - ${e.message}" }
        }
    }

    /**
     * Полный тест через NotificationService
     */
    suspend fun testFull() {
        logger.d { "QuickTest: запуск полного теста через NotificationService" }

        try {
            val notificationService = AppDependencies.container.notificationService

            if (notificationService is AndroidNotificationService) {
                // Отправляем немедленное уведомление
                notificationService.sendTestNotification("🔥 ПОЛНЫЙ ТЕСТ", "Проверка через NotificationService")

                // Планируем на 15 секунд
                notificationService.scheduleTestTimeReminder()

                // Запускаем диагностику
                val diagnostics = notificationService.runPermissionDiagnostics()
                logger.d { "QuickTest диагностика:\n$diagnostics" }

                logger.d { "QuickTest: полный тест запущен" }
            } else {
                logger.w { "QuickTest: NotificationService недоступен" }
            }
        } catch (e: Exception) {
            logger.e(e) { "QuickTest: ошибка полного теста - ${e.message}" }
        }
    }

    /**
     * Диагностика разрешений
     */
    fun checkPermissions(context: Context): String {
        logger.d { "QuickTest: проверка разрешений" }

        try {
            return NotificationDiagnostics.getDiagnosticsReport(context)
        } catch (e: Exception) {
            logger.e(e) { "QuickTest: ошибка проверки разрешений - ${e.message}" }
            return "Ошибка диагностики: ${e.message}"
        }
    }
}

/**
 * Глобальные функции для быстрого доступа из любого места
 */

/**
 * Быстрый тест уведомления - вызывайте из любого Activity или сервиса
 */
fun quickTestNotification(context: Context) {
    QuickNotificationTest.testNow(context)
}

/**
 * Быстрый тест алама - вызывайте из любого Activity или сервиса
 */
fun quickTestAlarm(context: Context) {
    QuickNotificationTest.testAlarm(context)
}
