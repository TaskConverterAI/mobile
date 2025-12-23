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
        logger.d { "Инициализация AndroidNotificationService" }

        // Создаем канал уведомлений
        notificationHelper.ensureChannel()

        // Устанавливаем callback для получения обновлений местоположения
        LocationTrackingService.onLocationUpdate = { latitude, longitude ->
            kotlinx.coroutines.runBlocking {
                checkLocationProximity(latitude, longitude)
            }
        }

        // Логируем текущий статус разрешений для отладки
        logCurrentStatus()
    }

    override suspend fun scheduleTaskNotifications(task: Task) {
        logger.d { "Настройка уведомлений для задачи ${task.id}" }

        // Получаем статус всех разрешений
        val permissions = checkAllPermissions()

        if (!permissions.hasBasicPermissions) {
            logger.w { "Отсутствуют базовые разрешения на уведомления" }
            showInAppNotification(
                "Нет разрешений",
                "Предоставьте разрешение на уведомления в настройках Android"
            )
            return
        }

        // Проверяем конкретные типы уведомлений
        val needsTimeNotifications = task.dueDate?.remindByTime == true
        val needsLocationNotifications = task.geotag?.remindByLocation == true

        if (needsTimeNotifications && !permissions.canUseTimeNotifications) {
            logger.w { "Нельзя использовать временные уведомления без разрешения на точные алармы" }
            showInAppNotification(
                "Требуются точные алармы",
                "Для временных напоминаний нужно разрешение на точные алармы"
            )
            requestExactAlarmPermissions()
            // Не возвращаемся - пробуем настроить хотя бы геоуведомления
        }

        if (needsLocationNotifications && !permissions.canUseLocationNotifications) {
            logger.w { "Нельзя использовать геоуведомления без разрешения на местоположение" }
            showInAppNotification(
                "Требуется доступ к местоположению",
                "Для геонапоминаний нужно разрешение на местоположение"
            )
            // Продолжаем - можем настроить временные уведомления
        }

        // Отменяем старые уведомления
        cancelTaskNotifications(task.id)

        // Настраиваем уведомления по времени если возможно
        if (needsTimeNotifications && permissions.canUseTimeNotifications) {
            logger.d { "Настраиваем временные уведомления для задачи ${task.id}" }
            scheduleTimeNotifications(task)
        } else if (needsTimeNotifications) {
            logger.w { "Пропускаем временные уведомления - нет разрешений" }
        }

        // Настраиваем уведомления по геопозиции если возможно
        if (needsLocationNotifications && permissions.canUseLocationNotifications) {
            logger.d { "Настраиваем геоуведомления для задачи ${task.id}" }
            scheduleLocationNotifications(task)
        } else if (needsLocationNotifications) {
            logger.w { "Пропускаем геоуведомления - нет разрешений" }
        }

        // Информируем о результате
        val scheduledTypes = mutableListOf<String>()
        if (needsTimeNotifications && permissions.canUseTimeNotifications) {
            scheduledTypes.add("временные")
        }
        if (needsLocationNotifications && permissions.canUseLocationNotifications) {
            scheduledTypes.add("геонапоминания")
        }

        if (scheduledTypes.isNotEmpty()) {
            logger.d { "Уведомления для задачи ${task.id} успешно настроены: ${scheduledTypes.joinToString(", ")}" }
        } else {
            logger.w { "Не удалось настроить ни одного типа уведомлений для задачи ${task.id}" }
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

        // Планируем фоновые напоминания через AlarmManager
        scheduleAlarmManagerReminders(task, remindersToSchedule)
    }

    /**
     * Планирует временные напоминания через AlarmManager для фонового срабатывания
     */
    private fun scheduleAlarmManagerReminders(task: Task, reminders: List<TimeReminder>) {
        logger.d { "Планируем ${reminders.size} напоминаний через AlarmManager для задачи '${task.title}'" }

        // Проверяем разрешения на планирование алармов
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!hasExactAlarmPermissions()) {
                logger.w { "Нет разрешения на точные алармы, используем fallback планирование" }
                // Показываем уведомление пользователю
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                    showInAppNotification(
                        "Ограниченная функциональность",
                        "Временные напоминания могут работать неточно без разрешения на точные алармы"
                    )
                }
            }
        }

        reminders.forEach { reminder ->
            val reminderType = when {
                reminder.triggerTime <= System.currentTimeMillis() + 60 * 60 * 1000L -> "за 1 час"
                reminder.triggerTime <= System.currentTimeMillis() + 24 * 60 * 60 * 1000L -> "за 1 день"
                else -> "за 3 дня"
            }

            try {
                TimeReminderReceiver.scheduleReminder(
                    context = context,
                    taskId = reminder.taskId,
                    taskTitle = reminder.taskTitle,
                    triggerTime = reminder.triggerTime,
                    reminderType = reminderType
                )
                logger.d { "Успешно запланирован алarm для задачи ${reminder.taskId} ($reminderType)" }
            } catch (e: Exception) {
                logger.e(e) { "Ошибка при планировании аларма для задачи ${reminder.taskId}: ${e.message}" }

                // Показываем уведомление об ошибке
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                    showInAppNotification(
                        "Ошибка планирования",
                        "Не удалось запланировать напоминание $reminderType для задачи '${task.title}'"
                    )
                }
            }
        }
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

        // Отменяем запланированные AlarmManager напоминания
        TimeReminderReceiver.cancelReminder(context, taskId)

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
        logger.d { "Проверка разрешений на уведомления" }

        try {
            // Сначала проверяем системные уведомления
            val notificationManager = androidx.core.app.NotificationManagerCompat.from(context)
            val areNotificationsEnabled = notificationManager.areNotificationsEnabled()
            logger.d { "Системные уведомления включены: $areNotificationsEnabled" }

            if (!areNotificationsEnabled) {
                logger.w { "Уведомления отключены на системном уровне" }
                return false
            }

            // Для Android 13+ дополнительно проверяем разрешение POST_NOTIFICATIONS
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                val hasPostNotificationsPermission = ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                logger.d { "POST_NOTIFICATIONS разрешение: $hasPostNotificationsPermission" }
                return hasPostNotificationsPermission
            }

            // Для Android < 13 достаточно системных настроек
            logger.d { "Android < 13, разрешения на уведомления: $areNotificationsEnabled" }
            return areNotificationsEnabled

        } catch (e: Exception) {
            logger.e(e) { "Ошибка при проверке разрешений на уведомления: ${e.message}" }
            return false
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
     * Проверяет разрешения на планирование точных алармов
     */
    fun hasExactAlarmPermissions(): Boolean {
        return AlarmPermissionHelper.canScheduleExactAlarms(context)
    }

    /**
     * Запрашивает разрешения на точные алармы (требует Activity)
     */
    fun requestExactAlarmPermissions() {
        logger.d { "Проверка разрешений на точные алармы" }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val canSchedule = hasExactAlarmPermissions()
            logger.d { "Разрешение на точные алармы: $canSchedule" }

            if (!canSchedule) {
                logger.w { "Нет разрешения на точные алармы, открываем настройки" }
                try {
                    AlarmPermissionHelper.requestExactAlarmPermission(context)
                } catch (e: Exception) {
                    logger.e(e) { "Ошибка при запросе разрешения на алармы: ${e.message}" }
                }
            } else {
                logger.d { "Разрешение на точные алармы уже предоставлено" }
            }
        } else {
            logger.d { "Android версии ниже 12, разрешение на алармы не требуется" }
        }
    }

    /**
     * Получает информацию о разрешениях на алармы
     */
    fun getExactAlarmPermissionInfo(): String {
        return AlarmPermissionHelper.getExactAlarmPermissionInfo()
    }

    /**
     * Проверяет все необходимые разрешения для уведомлений
     */
    suspend fun checkAllPermissions(): PermissionStatus {
        val hasNotifications = hasNotificationPermissions()
        val hasLocation = hasLocationPermissions()
        val hasAlarms = hasExactAlarmPermissions()

        logger.d { "Статус разрешений: Уведомления=$hasNotifications, Местоположение=$hasLocation, Алармы=$hasAlarms" }

        return PermissionStatus(
            notifications = hasNotifications,
            location = hasLocation,
            exactAlarms = hasAlarms
        )
    }

    /**
     * Статус разрешений для уведомлений
     */
    data class PermissionStatus(
        val notifications: Boolean,
        val location: Boolean,
        val exactAlarms: Boolean
    ) {
        val allGranted: Boolean get() = notifications && location && exactAlarms
        val hasBasicPermissions: Boolean get() = notifications
        val canUseLocationNotifications: Boolean get() = notifications && location
        val canUseTimeNotifications: Boolean get() = notifications && (exactAlarms || android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S)

        fun getMissingPermissions(): List<String> {
            val missing = mutableListOf<String>()
            if (!notifications) missing.add("Уведомления")
            if (!location) missing.add("Местоположение")
            if (!exactAlarms && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) missing.add("Точные алармы")
            return missing
        }
    }

    /**
     * Логирует текущий статус разрешений для отладки
     */
    fun logCurrentStatus() {
        NotificationDiagnostics.logPermissionStatus(context)
        logger.d { "Активных геонапоминаний: ${activeLocationReminders.size}" }
        logger.d { "Активных временных напоминаний: ${activeTimeReminders.size}" }
    }

    /**
     * ТЕСТОВАЯ ФУНКЦИЯ: Принудительно отправить push уведомление для проверки
     */
    fun sendTestNotification(title: String = "Тест уведомления", message: String = "Проверка работы уведомлений") {
        logger.d { "Отправка тестового уведомления: '$title' - '$message'" }

        try {
            // Проверяем канал
            notificationHelper.ensureChannel()

            // Отправляем уведомление с тестовым ID
            notificationHelper.showTaskNotification(
                taskId = 9999L,
                title = title,
                body = message
            )

            logger.d { "Тестовое уведомление отправлено успешно" }

            // Также показываем in-app уведомление через корутину
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                showInAppNotification("Тест отправлен", "Push уведомление '$title' должно появиться")
            }

        } catch (e: Exception) {
            logger.e(e) { "Ошибка при отправке тестового уведомления: ${e.message}" }
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                showInAppNotification("Ошибка теста", "Не удалось отправить тестовое уведомление: ${e.message}")
            }
        }
    }

    /**
     * ТЕСТОВАЯ ФУНКЦИЯ: Запланировать тестовое напоминание через 30 секунд
     */
    fun scheduleTestTimeReminder() {
        logger.d { "Планирование тестового напоминания через 30 секунд" }

        try {
            val triggerTime = System.currentTimeMillis() + 30 * 1000L // +30 секунд

            TimeReminderReceiver.scheduleReminder(
                context = context,
                taskId = 9998L,
                taskTitle = "ТЕСТ: Временное напоминание",
                triggerTime = triggerTime,
                reminderType = "тестовое через 30 сек"
            )

            logger.d { "Тестовое временное напоминание запланировано на $triggerTime" }
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                showInAppNotification("Тест запланирован", "Уведомление должно прийти через 30 секунд")
            }

        } catch (e: Exception) {
            logger.e(e) { "Ошибка при планировании тестового напоминания: ${e.message}" }
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                showInAppNotification("Ошибка планирования", "Не удалось запланировать тест: ${e.message}")
            }
        }
    }

    /**
     * ТЕСТОВАЯ ФУНКЦИЯ: Проверить все разрешения и показать подробный отчет
     */
    suspend fun runPermissionDiagnostics(): String {
        logger.d { "Запуск полной диагностики разрешений" }

        val report = StringBuilder()
        report.appendLine("=== ДИАГНОСТИКА РАЗРЕШЕНИЙ ===")

        try {
            // Проверяем уведомления
            val hasNotifications = hasNotificationPermissions()
            report.appendLine("✓ Разрешение на уведомления: $hasNotifications")

            // Проверяем местоположение
            val hasLocation = hasLocationPermissions()
            report.appendLine("✓ Разрешение на местоположение: $hasLocation")

            // Проверяем точные алармы
            val hasAlarms = hasExactAlarmPermissions()
            report.appendLine("✓ Разрешение на точные алармы: $hasAlarms")

            // Проверяем канал уведомлений
            try {
                notificationHelper.ensureChannel()
                report.appendLine("✓ Канал уведомлений создан успешно")
            } catch (e: Exception) {
                report.appendLine("✗ Ошибка создания канала: ${e.message}")
            }

            // Пробуем отправить тестовое уведомление
            try {
                sendTestNotification("Диагностика", "Тестовое уведомление в рамках диагностики")
                report.appendLine("✓ Тестовое уведомление отправлено")
            } catch (e: Exception) {
                report.appendLine("✗ Ошибка отправки тестового уведомления: ${e.message}")
            }

            report.appendLine()
            report.appendLine("Общий статус:")
            val permissions = checkAllPermissions()
            if (permissions.allGranted) {
                report.appendLine("✅ Все разрешения предоставлены")
            } else {
                report.appendLine("❌ Отсутствуют разрешения: ${permissions.getMissingPermissions()}")
            }

        } catch (e: Exception) {
            report.appendLine("❌ КРИТИЧЕСКАЯ ОШИБКА: ${e.message}")
            logger.e(e) { "Ошибка при диагностике разрешений" }
        }

        val result = report.toString()
        logger.d { "Результат диагностики:\n$result" }
        return result
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
