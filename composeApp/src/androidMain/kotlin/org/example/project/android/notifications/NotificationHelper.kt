package org.example.project.android.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.example.project.MainActivity

class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "geo_reminders"
        const val EXTRA_TASK_ID = "extra_task_id"
    }

    fun ensureChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Геонапоминания",
            NotificationManager.IMPORTANCE_HIGH // Повышаем приоритет для лучшей видимости
        ).apply {
            description = "Уведомления о приближении к местам выполнения задач"
            setShowBadge(true)
        }
        val nm = context.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }

    /**
     * Показать системное уведомление с переходом к задаче
     */
    fun showTaskNotification(taskId: Long, title: String, body: String) {
        // Проверяем разрешения перед отправкой
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return // Нет разрешения на уведомления
            }
        }

        // Создаем Intent для открытия конкретной задачи
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TASK_ID, taskId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(), // Уникальный ID для каждой задачи
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body)) // Для длинных текстов
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // При клике откроется задача
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        try {
            NotificationManagerCompat.from(context).notify(taskId.toInt(), builder.build())
        } catch (e: SecurityException) {
            // Обрабатываем случай когда нет разрешений
            co.touchlab.kermit.Logger.withTag("NotificationHelper")
                .w(e) { "Нет разрешений для отправки уведомления" }
        }
    }

    /**
     * Обратная совместимость (deprecated, используйте showTaskNotification)
     */
    @Deprecated("Используйте showTaskNotification для лучшей функциональности")
    fun showSystemNotification(noteId: Long, title: String, body: String) {
        showTaskNotification(noteId, title, body)
    }
}
