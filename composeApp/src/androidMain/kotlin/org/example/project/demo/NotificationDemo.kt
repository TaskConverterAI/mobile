package org.example.project.demo

import org.example.project.AppDependencies
import org.example.project.android.notifications.AndroidNotificationService
import org.example.project.android.location.LocationMocker
import org.example.project.data.commonData.*
import co.touchlab.kermit.Logger
import kotlin.time.ExperimentalTime

/**
 * Демонстрационная утилита для тестирования системы уведомлений
 */
object NotificationDemo {

    private val logger = Logger.withTag("NotificationDemo")

    /**
     * Создать тестовую задачу с геонапоминанием
     */
    @OptIn(ExperimentalTime::class)
    fun createTestTaskWithGeoReminder(
        location: TestLocation = TestLocation.RED_SQUARE
    ): Task {
        val testTask = Task(
            id = 999L,
            title = "Тестовая задача: ${location.displayName}",
            description = "Эта задача создана для тестирования системы геонапоминаний в локации ${location.displayName}",
            comments = emptyList(),
            authorId = 1L,
            groupId = null,
            assignee = 1L,
            dueDate = Deadline(
                time = kotlin.time.Clock.System.now().toEpochMilliseconds() + 3600000, // +1 час
                remindByTime = true
            ),
            geotag = Location(
                latitude = location.lat,
                longitude = location.lon,
                name = location.displayName,
                remindByLocation = true
            ),
            priority = Priority.HIGH,
            status = Status.UNDONE,
            createAt = kotlin.time.Clock.System.now().toEpochMilliseconds()
        )

        logger.d { "Создана тестовая задача: ${testTask.title} в точке (${location.lat}, ${location.lon})" }
        return testTask
    }

    /**
     * Предустановленные локации для тестирования
     */
    enum class TestLocation(val displayName: String, val lat: Double, val lon: Double) {
        RED_SQUARE("Красная площадь", 55.7558, 37.6173),
        MOSCOW_STATE_UNIVERSITY("МГУ", 55.7033, 37.5295),
        BOLSHOI_THEATRE("Большой театр", 55.7601, 37.6184),
        KREMLIN("Кремль", 55.7520, 37.6175),
        ARBAT("Арбат", 55.7520, 37.5924),
        TVERSKAYA("Тверская", 55.7658, 37.6037),
        GORKY_PARK("Парк Горького", 55.7312, 37.6014),
        VDNKh("ВДНХ", 55.8271, 37.6394)
    }

    /**
     * Типы демонстрации
     */
    enum class DemoType {
        QUICK,      // Быстрая демонстрация
        REALISTIC,  // Реалистичное движение
        RANDOM,     // Случайное блуждание
        SHOWCASE    // Показ всех типов движения
    }

    /**
     * Запустить демонстрацию уведомлений
     */
    suspend fun runNotificationDemo(
        demoType: DemoType = DemoType.QUICK,
        location: TestLocation = TestLocation.RED_SQUARE
    ) {
        try {
            val notificationService = AppDependencies.container.notificationService

            if (notificationService !is AndroidNotificationService) {
                logger.w { "NotificationService не является AndroidNotificationService" }
                return
            }

            // Создаем тестовую задачу
            val testTask = createTestTaskWithGeoReminder(location)

            // Настраиваем уведомления
            notificationService.scheduleTaskNotifications(testTask)

            logger.d { "Демонстрация $demoType запущена. Активные напоминания: ${notificationService.getActiveReminders().size}" }

            // Показываем информацию об активных напоминаниях
            notificationService.getActiveReminders().forEach { (taskId, reminder) ->
                logger.d { "Активное напоминание для задачи $taskId в точке (${reminder.latitude}, ${reminder.longitude})" }
            }

            // Запускаем соответствующий тип тестирования
            when (demoType) {
                DemoType.QUICK -> {
                    notificationService.startFastLocationTesting(testTask.id)
                }
                DemoType.REALISTIC -> {
                    notificationService.startRealisticLocationTesting(testTask.id)
                }
                DemoType.RANDOM -> {
                    notificationService.startRandomWalkTesting(testTask.id)
                }
                DemoType.SHOWCASE -> {
                    runShowcaseDemo(notificationService, testTask.id)
                }
            }

        } catch (e: Exception) {
            logger.e(e) { "Ошибка при запуске демонстрации: ${e.message}" }
        }
    }

    /**
     * Простой запуск демо (для совместимости)
     */
    suspend fun runNotificationDemo() {
        runNotificationDemo(DemoType.QUICK, TestLocation.RED_SQUARE)
    }

    /**
     * Запустить демонстрацию всех типов движения по очереди
     */
    private suspend fun runShowcaseDemo(
        notificationService: AndroidNotificationService,
        taskId: Long
    ) {
        logger.d { "Запуск showcase демонстрации - все типы движения" }

        // 1. Быстрый тест
        logger.d { "1/4 - Быстрое линейное движение" }
        notificationService.startFastLocationTesting(taskId)
        kotlinx.coroutines.delay(15000) // 15 секунд
        notificationService.stopLocationTesting()

        kotlinx.coroutines.delay(2000) // Пауза между тестами

        // 2. Кривое движение
        logger.d { "2/4 - Движение по кривой" }
        notificationService.startCustomLocationTesting(
            taskId,
            LocationMocker.MockingConfig(
                type = LocationMocker.MockingType.CURVED,
                stepCount = 15,
                updateIntervalMs = 1500
            )
        )
        kotlinx.coroutines.delay(25000)
        notificationService.stopLocationTesting()

        kotlinx.coroutines.delay(2000)

        // 3. Случайное блуждание
        logger.d { "3/4 - Случайное блуждание" }
        notificationService.startRandomWalkTesting(taskId)
        kotlinx.coroutines.delay(30000)
        notificationService.stopLocationTesting()

        kotlinx.coroutines.delay(2000)

        // 4. Реалистичное движение
        logger.d { "4/4 - Реалистичное движение" }
        notificationService.startRealisticLocationTesting(taskId)

        logger.d { "Showcase демонстрация завершена. Финальный тест будет идти до завершения." }
    }

    /**
     * Запустить демонстрацию кругового движения
     */
    suspend fun runCircularDemo(location: TestLocation = TestLocation.RED_SQUARE) {
        try {
            val notificationService = AppDependencies.container.notificationService

            if (notificationService !is AndroidNotificationService) {
                logger.w { "NotificationService не является AndroidNotificationService" }
                return
            }

            val testTask = createTestTaskWithGeoReminder(location)
            notificationService.scheduleTaskNotifications(testTask)

            logger.d { "Запуск демонстрации кругового движения" }

            notificationService.startCustomLocationTesting(
                testTask.id,
                LocationMocker.MockingConfig(
                    type = LocationMocker.MockingType.CIRCULAR,
                    stepCount = 20,
                    updateIntervalMs = 1500
                )
            )

        } catch (e: Exception) {
            logger.e(e) { "Ошибка при запуске кругового демо: ${e.message}" }
        }
    }

    /**
     * Остановить демонстрацию
     */
    suspend fun stopNotificationDemo() {
        try {
            val notificationService = AppDependencies.container.notificationService

            if (notificationService is AndroidNotificationService) {
                val testingInfo = notificationService.getLocationTestingInfo()
                if (testingInfo != null) {
                    logger.d { "Остановка активного тестирования: $testingInfo" }
                }

                notificationService.stopLocationTesting()
                notificationService.cancelTaskNotifications(999L)
                logger.d { "Демонстрация остановлена" }
            }
        } catch (e: Exception) {
            logger.e(e) { "Ошибка при остановке демонстрации: ${e.message}" }
        }
    }

    /**
     * Получить информацию о текущей демонстрации
     */
    suspend fun getDemoStatus(): String {
        return try {
            val notificationService = AppDependencies.container.notificationService

            if (notificationService is AndroidNotificationService) {
                val testingInfo = notificationService.getLocationTestingInfo()
                val activeReminders = notificationService.getActiveReminders().size

                when {
                    testingInfo != null -> "Тестирование: $testingInfo, напоминаний: $activeReminders"
                    activeReminders > 0 -> "Ожидание тестирования, напоминаний: $activeReminders"
                    else -> "Демо не активно"
                }
            } else {
                "NotificationService недоступен"
            }
        } catch (e: Exception) {
            "Ошибка: ${e.message}"
        }
    }

    /**
     * НОВАЯ ФУНКЦИЯ: Тестирование push уведомлений
     */
    suspend fun testPushNotifications(): String {
        return try {
            val notificationService = AppDependencies.container.notificationService

            if (notificationService !is AndroidNotificationService) {
                logger.w { "NotificationService не является AndroidNotificationService" }
                return "❌ NotificationService недоступен"
            }

            logger.d { "Запуск теста push уведомлений" }

            // Отправляем тестовое уведомление немедленно
            notificationService.sendTestNotification(
                title = "🔔 ТЕСТ УВЕДОМЛЕНИЯ",
                message = "Если вы видите это уведомление, то push уведомления работают!"
            )

            // Планируем тестовое напоминание через 30 секунд
            notificationService.scheduleTestTimeReminder()

            logger.d { "Тесты уведомлений запущены" }
            "✅ Тесты запущены: немедленное уведомление + уведомление через 30 сек"

        } catch (e: Exception) {
            logger.e(e) { "Ошибка при тестировании уведомлений: ${e.message}" }
            "❌ Ошибка: ${e.message}"
        }
    }

    /**
     * НОВАЯ ФУНКЦИЯ: Полная диагностика разрешений
     */
    suspend fun runFullDiagnostics(): String {
        return try {
            val notificationService = AppDependencies.container.notificationService

            if (notificationService !is AndroidNotificationService) {
                return "❌ NotificationService не является AndroidNotificationService"
            }

            logger.d { "Запуск полной диагностики системы уведомлений" }

            val diagnostics = notificationService.runPermissionDiagnostics()
            logger.d { "Диагностика завершена" }

            diagnostics

        } catch (e: Exception) {
            logger.e(e) { "Ошибка при диагностике: ${e.message}" }
            "❌ Ошибка диагностики: ${e.message}"
        }
    }

    /**
     * НОВАЯ ФУНКЦИЯ: Создать задачу с немедленным временным напоминанием для теста
     */
    @OptIn(ExperimentalTime::class)
    suspend fun createTestTaskWithTimeReminder(): Task {
        val currentTime = kotlin.time.Clock.System.now().toEpochMilliseconds()

        val testTask = Task(
            id = 9997L,
            title = "🕒 Тест временного напоминания",
            description = "Эта задача создана для тестирования временных напоминаний. Напоминания должны прийти за 3 дня, 1 день и 1 час до дедлайна.",
            comments = emptyList(),
            authorId = 1L,
            groupId = null,
            assignee = 1L,
            dueDate = Deadline(
                time = currentTime + 2 * 60 * 1000, // Дедлайн через 2 минуты
                remindByTime = true
            ),
            geotag = null, // Без геонапоминаний для этого теста
            priority = Priority.HIGH,
            status = Status.UNDONE,
            createAt = currentTime
        )

        logger.d { "Создана тестовая задача с временным напоминанием: дедлайн через 2 минуты" }
        return testTask
    }

    /**
     * НОВАЯ ФУНКЦИЯ: Тест временных напоминаний
     */
    suspend fun testTimeReminders(): String {
        return try {
            val notificationService = AppDependencies.container.notificationService

            if (notificationService !is AndroidNotificationService) {
                return "❌ NotificationService недоступен"
            }

            logger.d { "Запуск теста временных напоминаний" }

            // Создаем задачу с дедлайном через 2 минуты
            val testTask = createTestTaskWithTimeReminder()

            // Планируем уведомления
            notificationService.scheduleTaskNotifications(testTask)

            logger.d { "Временные напоминания запланированы для задачи ${testTask.id}" }

            "✅ Тест временных напоминаний запущен:\n" +
            "- Задача: '${testTask.title}'\n" +
            "- Дедлайн: через 2 минуты\n" +
            "- Ожидайте уведомления за 1 час до дедлайна (должно прийти немедленно)"

        } catch (e: Exception) {
            logger.e(e) { "Ошибка при тестировании временных напоминаний: ${e.message}" }
            "❌ Ошибка: ${e.message}"
        }
    }

    /**
     * НОВАЯ ФУНКЦИЯ: Комплексный тест всех типов уведомлений
     */
    suspend fun runCompleteTest(): String {
        return try {
            logger.d { "Запуск комплексного теста уведомлений" }

            val results = StringBuilder()
            results.appendLine("🧪 КОМПЛЕКСНЫЙ ТЕСТ УВЕДОМЛЕНИЙ")
            results.appendLine()

            // 1. Диагностика
            results.appendLine("1️⃣ ДИАГНОСТИКА РАЗРЕШЕНИЙ:")
            val diagnostics = runFullDiagnostics()
            results.appendLine(diagnostics)
            results.appendLine()

            // 2. Тест push уведомлений
            results.appendLine("2️⃣ ТЕСТ PUSH УВЕДОМЛЕНИЙ:")
            val pushTest = testPushNotifications()
            results.appendLine(pushTest)
            results.appendLine()

            // 3. Тест временных напоминаний
            results.appendLine("3️⃣ ТЕСТ ВРЕМЕННЫХ НАПОМИНАНИЙ:")
            val timeTest = testTimeReminders()
            results.appendLine(timeTest)
            results.appendLine()

            // 4. Тест геонапоминаний
            results.appendLine("4️⃣ ТЕСТ ГЕОНАПОМИНАНИЙ:")
            runNotificationDemo(DemoType.QUICK, TestLocation.RED_SQUARE)
            results.appendLine("✅ Геодемонстрация запущена (Красная площадь)")
            results.appendLine()

            results.appendLine("🎯 Комплексный тест завершен!")
            results.appendLine("Следите за уведомлениями в течение следующих 2-3 минут")

            val result = results.toString()
            logger.d { "Комплексный тест завершен:\n$result" }

            result

        } catch (e: Exception) {
            logger.e(e) { "Ошибка при комплексном тестировании: ${e.message}" }
            "❌ Ошибка комплексного теста: ${e.message}"
        }
    }
}
