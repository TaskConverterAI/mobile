package org.example.project.data.notifications

import kotlinx.coroutines.flow.StateFlow
import org.example.project.data.commonData.Task

/**
 * Интерфейс для управления уведомлениями задач
 */
interface NotificationService {

    /**
     * StateFlow для отслеживания in-app уведомлений
     */
    val inAppNotificationsFlow: StateFlow<String?>

    /**
     * Настроить все уведомления для задачи
     */
    suspend fun scheduleTaskNotifications(task: Task)

    /**
     * Настроить уведомления по времени
     */
    suspend fun scheduleTimeNotifications(task: Task)

    /**
     * Настроить уведомления по геопозиции
     */
    suspend fun scheduleLocationNotifications(task: Task)

    /**
     * Отменить все уведомления для задачи
     */
    suspend fun cancelTaskNotifications(taskId: Long)

    /**
     * Показать in-app уведомление
     */
    suspend fun showInAppNotification(title: String, message: String)

    /**
     * Очистить in-app уведомление
     */
    fun clearInAppNotification()

    /**
     * Показать push уведомление
     */
    suspend fun showPushNotification(title: String, message: String, taskId: Long)

    /**
     * Проверить разрешения на уведомления
     */
    suspend fun hasNotificationPermissions(): Boolean

    /**
     * Запросить разрешения на уведомления
     */
    suspend fun requestNotificationPermissions(): Boolean

    /**
     * Проверить разрешения на геолокацию
     */
    suspend fun hasLocationPermissions(): Boolean

    /**
     * Запросить разрешения на геолокацию
     */
    suspend fun requestLocationPermissions(): Boolean
}
