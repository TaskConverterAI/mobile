package org.example.project.android.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimeReminderReceiver : BroadcastReceiver() {
    private val logger = Logger.withTag("TimeReminderReceiver")

    companion object {
        const val ACTION_TIME_REMINDER = "org.example.project.TIME_REMINDER"
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_REMINDER_TYPE = "extra_reminder_type"

        /**
         * Планирует напоминание через AlarmManager
         */
        fun scheduleReminder(
            context: Context,
            taskId: Long,
            taskTitle: String,
            triggerTime: Long,
            reminderType: String
        ) {
            val logger = Logger.withTag("TimeReminderReceiver")
            logger.d { "Планирование напоминания: taskId=$taskId, title='$taskTitle', time=$triggerTime ($reminderType)" }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimeReminderReceiver::class.java).apply {
                action = ACTION_TIME_REMINDER
                putExtra(EXTRA_TASK_ID, taskId)
                putExtra(EXTRA_TASK_TITLE, taskTitle)
                putExtra(EXTRA_REMINDER_TYPE, reminderType)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId.toInt(), // Используем taskId как requestCode для уникальности
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Проверяем, что время в будущем
            val currentTime = System.currentTimeMillis()
            if (triggerTime <= currentTime) {
                logger.w { "Время напоминания в прошлом: $triggerTime <= $currentTime, пропускаем" }
                return
            }

            val timeUntilTrigger = triggerTime - currentTime
            logger.d { "Напоминание сработает через ${timeUntilTrigger / 1000} секунд" }

            try {
                // Проверяем возможность планировать точные алармы (Android 12+)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    val canScheduleExact = alarmManager.canScheduleExactAlarms()
                    logger.d { "Разрешение на точные алармы: $canScheduleExact" }

                    if (!canScheduleExact) {
                        logger.w { "Нет разрешения на точные алармы, используем обычный setAndAllowWhileIdle" }

                        // Используем менее точный метод, но который не требует специального разрешения
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent
                        )
                        logger.d { "Запланировано неточное напоминание для задачи $taskId на время $triggerTime ($reminderType)" }
                        return
                    }
                }

                // Используем setExactAndAllowWhileIdle для точного срабатывания даже в doze mode
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )

                logger.d { "Запланировано точное напоминание для задачи $taskId на время $triggerTime ($reminderType)" }
            } catch (e: SecurityException) {
                // Если нет разрешения на точные алармы, используем обычный метод
                logger.w { "SecurityException при планировании точного алама, используем setAndAllowWhileIdle: ${e.message}" }

                try {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                    logger.d { "Запланировано неточное напоминание для задачи $taskId на время $triggerTime ($reminderType)" }
                } catch (e2: Exception) {
                    logger.e(e2) { "Ошибка при планировании неточного алама: ${e2.message}" }
                }
            } catch (e: Exception) {
                logger.e(e) { "Ошибка при планировании напоминания: ${e.message}" }
            }
        }

        /**
         * Отменяет запланированное напоминание
         */
        fun cancelReminder(context: Context, taskId: Long) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimeReminderReceiver::class.java).apply {
                action = ACTION_TIME_REMINDER
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()

            val logger = Logger.withTag("TimeReminderReceiver")
            logger.d { "Отменено напоминание для задачи $taskId" }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_TIME_REMINDER) {
            val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
            val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Задача"
            val reminderType = intent.getStringExtra(EXTRA_REMINDER_TYPE) ?: "напоминание"

            logger.d { "Получено временное напоминание для задачи $taskId ($reminderType)" }

            // Показываем уведомление в фоновом режиме
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val notificationHelper = NotificationHelper(context)
                    val title = "Напоминание о задаче"
                    val message = "Приближается дедлайн задачи: \"$taskTitle\" ($reminderType)"

                    notificationHelper.ensureChannel()
                    notificationHelper.showTaskNotification(taskId, title, message)

                    logger.d { "Push уведомление отправлено для задачи $taskId" }
                } catch (e: Exception) {
                    logger.e(e) { "Ошибка при отправке push уведомления: ${e.message}" }
                }
            }
        }
    }
}
