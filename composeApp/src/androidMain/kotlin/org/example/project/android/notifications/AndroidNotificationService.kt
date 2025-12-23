package org.example.project.android.notifications

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.android.location.LocationMocker
import org.example.project.data.commonData.Task
import org.example.project.data.notifications.NotificationService
import org.example.project.android.location.LocationTrackingService
import org.example.project.android.permissions.LocationPermissionHelper

// Модель для хранения напоминаний с дополнительной информацией
data class SimpleLocationReminder(
    val taskId: Long,
    val taskTitle: String, // Название задачи для уведомлений
    val latitude: Double,
    val longitude: Double,
    val radius: Double = 100.0,
    val isActive: Boolean = true,
    var lastNotificationTime: Long = 0L // Время последнего уведомления (millis)
)

// Модель для временных напоминаний
data class TimeReminder(
    val taskId: Long,
    val taskTitle: String,
    val triggerTime: Long, // Время когда должно сработать напоминание
    var lastNotificationTime: Long = 0L
)

class AndroidNotificationService(
    private val context: Context,
    private val notificationHelper: NotificationHelper
) : NotificationService {

    private val logger = Logger.withTag("AndroidNotificationService")

    companion object {
        private const val NOTIFICATION_COOLDOWN_MS = 2 * 60 * 60 * 1000L // 2 часа в миллисекундах
    }

    // StateFlow для in-app уведомлений
    private val _inAppNotifications = MutableStateFlow<String?>(null)
    val inAppNotifications: StateFlow<String?> = _inAppNotifications.asStateFlow()

    // Реализация интерфейса для commonMain
    override val inAppNotificationsFlow: StateFlow<String?> = _inAppNotifications.asStateFlow()

    // Хранилище активных напоминаний
    private val activeLocationReminders = mutableMapOf<Long, SimpleLocationReminder>()
    private val activeTimeReminders = mutableMapOf<Long, TimeReminder>()

    // Отслеживание состояния приложения
    private var isAppInForeground = true

    /**
     * Установить состояние приложения (для выбора типа уведомления)
     */
    fun setAppForegroundState(inForeground: Boolean) {
        isAppInForeground = inForeground
        logger.d { "Состояние приложения изменено: ${if (inForeground) "на переднем плане" else "в фоне"}" }
    }

    init {
        // Устанавливаем callback для получения обновлений местоположения
        LocationTrackingService.onLocationUpdate = { latitude, longitude ->
            kotlinx.coroutines.runBlocking {
                checkLocationProximity(latitude, longitude)
            }
        }
    }

    override suspend fun scheduleTaskNotifications(task: Task) {
        logger.d { "Настройка уведомлений для задачи ${task.id}" }

        // Отменяем старые уведомления
        cancelTaskNotifications(task.id)

        // Настраиваем уведомления по времени
        if (task.dueDate?.remindByTime == true) {
            scheduleTimeNotifications(task)
        }

        // Настраиваем уведомления по геопозиции
        if (task.geotag?.remindByLocation == true) {
            scheduleLocationNotifications(task)
        }
    }

    override suspend fun scheduleTimeNotifications(task: Task) {
        val dueDate = task.dueDate?.time ?: return
        val currentTime = System.currentTimeMillis()

        logger.d { "Настройка временных уведомлений для задачи '${task.title}', дедлайн: $dueDate" }

        val remindersToSchedule = mutableListOf<TimeReminder>()

        // За 3 дня
        val threeDaysMs = 3 * 24 * 60 * 60 * 1000L
        val threeDaysBefore = dueDate - threeDaysMs
        if (threeDaysBefore > currentTime && (dueDate - task.createAt) > threeDaysMs) {
            remindersToSchedule.add(
                TimeReminder(
                    taskId = task.id,
                    taskTitle = task.title,
                    triggerTime = threeDaysBefore
                )
            )
            logger.d { "Запланировано уведомление за 3 дня для задачи '${task.title}'" }
        }

        // За 1 день
        val oneDayMs = 24 * 60 * 60 * 1000L
        val oneDayBefore = dueDate - oneDayMs
        if (oneDayBefore > currentTime && (dueDate - task.createAt) > oneDayMs) {
            remindersToSchedule.add(
                TimeReminder(
                    taskId = task.id,
                    taskTitle = task.title,
                    triggerTime = oneDayBefore
                )
            )
            logger.d { "Запланировано уведомление за 1 день для задачи '${task.title}'" }
        }

        // За 1 час
        val oneHourMs = 60 * 60 * 1000L
        val oneHourBefore = dueDate - oneHourMs
        if (oneHourBefore > currentTime && (dueDate - task.createAt) > oneHourMs) {
            remindersToSchedule.add(
                TimeReminder(
                    taskId = task.id,
                    taskTitle = task.title,
                    triggerTime = oneHourBefore
                )
            )
            logger.d { "Запланировано уведомление за 1 час для задачи '${task.title}'" }
        }

        // Добавляем напоминания в хранилище
        remindersToSchedule.forEach { reminder ->
            activeTimeReminders[reminder.taskId] = reminder
        }

        // TODO: Здесь можно добавить фоновые таймеры или AlarmManager для срабатывания в нужное время
        // Пока что временные напоминания будут обрабатываться при следующем открытии приложения
    }

    override suspend fun scheduleLocationNotifications(task: Task) {
        val geotag = task.geotag ?: return

        logger.d { "Настройка геонапоминания для задачи '${task.title}' в точке (${geotag.latitude}, ${geotag.longitude})" }

        val reminder = SimpleLocationReminder(
            taskId = task.id,
            taskTitle = task.title,
            latitude = geotag.latitude,
            longitude = geotag.longitude,
            radius = 100.0,
            isActive = true
        )

        activeLocationReminders[task.id] = reminder

        // Запускаем геотрекинг если есть активные напоминания
        startLocationTrackingIfNeeded()
    }

    override suspend fun cancelTaskNotifications(taskId: Long) {
        logger.d { "Отмена уведомлений для задачи $taskId" }

        // Удаляем геонапоминания
        activeLocationReminders.remove(taskId)

        // Удаляем временные напоминания
        activeTimeReminders.remove(taskId)

        // Останавливаем геотрекинг если нет активных геонапоминаний
        if (activeLocationReminders.isEmpty()) {
            stopLocationTracking()
        }

        logger.d { "Уведомления для задачи $taskId отменены" }
    }

    override suspend fun showInAppNotification(title: String, message: String) {
        logger.d { "Показ in-app уведомления: $title - $message" }
        val fullMessage = "$title: $message"

        logger.d { "Устанавливаем значение StateFlow: '$fullMessage'" }
        _inAppNotifications.value = fullMessage

        logger.d { "Текущее значение _inAppNotifications: '${_inAppNotifications.value}'" }
        logger.d { "Текущее значение inAppNotificationsFlow: '${inAppNotificationsFlow.value}'" }

        // Автоматически скрываем через 5 секунд в отдельной корутине
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            kotlinx.coroutines.delay(5000)
            if (_inAppNotifications.value == fullMessage) {
                // Очищаем только если сообщение не изменилось
                logger.d { "Автоочистка StateFlow через 5 секунд" }
                _inAppNotifications.value = null
            }
        }
    }

    override suspend fun showPushNotification(title: String, message: String, taskId: Long) {
        try {
            notificationHelper.ensureChannel()
            notificationHelper.showTaskNotification(taskId, title, message)
            logger.d { "Push уведомление отправлено для задачи $taskId: $title" }
        } catch (e: Exception) {
            logger.e(e) { "Ошибка при отправке push уведомления: ${e.message}" }
        }
    }

    override suspend fun hasNotificationPermissions(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    override suspend fun requestNotificationPermissions(): Boolean {
        // Этот метод должен вызываться из Activity для запроса разрешений
        return hasNotificationPermissions()
    }

    override suspend fun hasLocationPermissions(): Boolean {
        return LocationPermissionHelper.hasLocationPermission(context)
    }

    override suspend fun requestLocationPermissions(): Boolean {
        // Этот метод должен вызываться из Activity для запроса разрешений
        return hasLocationPermissions()
    }

    /**
     * Проверка близости к геометке
     * Вызывается сервисом отслеживания местоположения
     */
    suspend fun checkLocationProximity(currentLatitude: Double, currentLongitude: Double) {
        val currentTime = System.currentTimeMillis()

        activeLocationReminders.values.forEach { reminder ->
            if (!reminder.isActive) return@forEach

            val distance = calculateDistance(
                currentLatitude, currentLongitude,
                reminder.latitude, reminder.longitude
            )

            if (distance <= reminder.radius) {
                // Проверяем, прошло ли достаточно времени с последнего уведомления
                val timeSinceLastNotification = currentTime - reminder.lastNotificationTime

                if (timeSinceLastNotification >= NOTIFICATION_COOLDOWN_MS) {
                    logger.d { "Пользователь в радиусе ${reminder.radius}м от задачи '${reminder.taskTitle}'" }

                    // Обновляем время последнего уведомления
                    reminder.lastNotificationTime = currentTime

                    val title = "Геонапоминание"
                    val message = "Вы рядом с местом задачи:\n\"${reminder.taskTitle}\""

                    // Выбираем тип уведомления в зависимости от состояния приложения
                    if (isAppInForeground) {
                        // Приложение открыто - показываем toast
                        showInAppNotification(title, message)
                        logger.d { "Показан in-app toast для задачи '${reminder.taskTitle}'" }
                    } else {
                        // Приложение в фоне - показываем push уведомление
                        showPushNotification(title, message, reminder.taskId)
                        logger.d { "Отправлено push уведомление для задачи '${reminder.taskTitle}'" }
                    }
                } else {
                    val timeLeft = (NOTIFICATION_COOLDOWN_MS - timeSinceLastNotification) / (60 * 1000) // в минутах
                    logger.d { "Cooldown активен для задачи '${reminder.taskTitle}', осталось ${timeLeft} минут" }
                }
            }
        }
    }

    /**
     * Вычисление расстояния между двумя точками в метрах
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0].toDouble()
    }

    override fun clearInAppNotification() {
        logger.d { "Очистка in-app уведомления" }
        _inAppNotifications.value = null
        logger.d { "StateFlow очищен, текущее значение: '${_inAppNotifications.value}'" }
    }

    /**
     * Запустить геотрекинг если есть активные напоминания
     */
    private fun startLocationTrackingIfNeeded() {
        if (activeLocationReminders.isNotEmpty()) {
            startLocationTracking()
        }
    }

    /**
     * Запустить сервис отслеживания геопозиции
     */
    private fun startLocationTracking() {
        try {
            // Проверяем разрешения на местоположение
            if (!LocationPermissionHelper.hasLocationPermission(context)) {
                logger.w { "Нет разрешений на местоположение, геотрекинг не запущен" }
                return
            }

            logger.d { "Запуск сервиса отслеживания геопозиции" }
            LocationTrackingService.startLocationTracking(context)
        } catch (e: Exception) {
            logger.e(e) { "Ошибка при запуске сервиса отслеживания: ${e.message}" }
        }
    }

    /**
     * Остановить сервис отслеживания геопозиции
     */
    private fun stopLocationTracking() {
        try {
            logger.d { "Остановка сервиса отслеживания геопозиции" }
            LocationTrackingService.stopLocationTracking(context)
        } catch (e: Exception) {
            logger.e(e) { "Ошибка при остановке сервиса отслеживания: ${e.message}" }
        }
    }

    /**
     * Проверить, есть ли активные задачи с геонапоминаниями
     */
    fun hasActiveLocationReminders(): Boolean {
        return activeLocationReminders.isNotEmpty()
    }

    /**
     * Запустить тестирование геолокации для конкретной задачи
     * Эмулирует движение пользователя к месту задачи
     */
    fun startLocationTesting(taskId: Long) {
        val reminder = activeLocationReminders[taskId]
        if (reminder != null) {
            logger.d { "Запуск базового тестирования геолокации для задачи $taskId" }
            LocationMocker.QuickStart.linearToTarget(
                targetLat = reminder.latitude,
                targetLon = reminder.longitude
            ) { lat: Double, lon: Double ->
                kotlinx.coroutines.runBlocking {
                    checkLocationProximity(lat, lon)
                }
            }
        } else {
            logger.w { "Не найдено активное напоминание для задачи $taskId" }
        }
    }

    /**
     * Запустить реалистичное тестирование геолокации
     */
    fun startRealisticLocationTesting(taskId: Long) {
        val reminder = activeLocationReminders[taskId]
        if (reminder != null) {
            logger.d { "Запуск реалистичного тестирования геолокации для задачи $taskId" }
            LocationMocker.QuickStart.realisticToTarget(
                targetLat = reminder.latitude,
                targetLon = reminder.longitude
            ) { lat: Double, lon: Double ->
                kotlinx.coroutines.runBlocking {
                    checkLocationProximity(lat, lon)
                }
            }
        } else {
            logger.w { "Не найдено активное напоминание для задачи $taskId" }
        }
    }

    /**
     * Запустить быстрое тестирование (для демо)
     */
    fun startFastLocationTesting(taskId: Long) {
        val reminder = activeLocationReminders[taskId]
        if (reminder != null) {
            logger.d { "Запуск быстрого тестирования геолокации для задачи $taskId" }

            LocationMocker.QuickStart.fastTest(
                targetLat = reminder.latitude,
                targetLon = reminder.longitude
            ) { lat: Double, lon: Double ->
                kotlinx.coroutines.runBlocking {
                    checkLocationProximity(lat, lon)
                }
            }
        } else {
            logger.w { "Не найдено активное напоминание для задачи $taskId" }
        }
    }

    /**
     * Запустить случайное блуждание к цели
     */
    fun startRandomWalkTesting(taskId: Long) {
        val reminder = activeLocationReminders[taskId]
        if (reminder != null) {
            logger.d { "Запуск случайного блуждания к цели для задачи $taskId" }
            org.example.project.android.location.LocationMocker.QuickStart.randomWalkToTarget(
                targetLat = reminder.latitude,
                targetLon = reminder.longitude
            ) { lat: Double, lon: Double ->
                kotlinx.coroutines.runBlocking {
                    checkLocationProximity(lat, lon)
                }
            }
        } else {
            logger.w { "Не найдено активное напоминание для задачи $taskId" }
        }
    }

    /**
     * Запустить кастомное тестирование с конфигурацией
     */
    fun startCustomLocationTesting(
        taskId: Long,
        config: org.example.project.android.location.LocationMocker.MockingConfig
    ) {
        val reminder = activeLocationReminders[taskId]
        if (reminder != null) {
            logger.d { "Запуск кастомного тестирования ${config.type} для задачи $taskId" }
            org.example.project.android.location.LocationMocker.startMockLocation(
                targetLat = reminder.latitude,
                targetLon = reminder.longitude,
                config = config
            ) { lat: Double, lon: Double ->
                kotlinx.coroutines.runBlocking {
                    checkLocationProximity(lat, lon)
                }
            }
        } else {
            logger.w { "Не найдено активное напоминание для задачи $taskId" }
        }
    }

    /**
     * Остановить тестирование геолокации
     */
    fun stopLocationTesting() {
        logger.d { "Остановка тестирования геолокации" }
        org.example.project.android.location.LocationMocker.stopMockLocation()
    }

    /**
     * Получить информацию о текущем тестировании
     */
    fun getLocationTestingInfo(): String? {
        return if (org.example.project.android.location.LocationMocker.isMocking()) {
            val type = org.example.project.android.location.LocationMocker.getCurrentMockingType()
            "Активно: $type"
        } else {
            null
        }
    }

    /**
     * Получить список активных геонапоминаний для отладки
     */
    fun getActiveReminders(): Map<Long, SimpleLocationReminder> {
        return activeLocationReminders.toMap()
    }
}
