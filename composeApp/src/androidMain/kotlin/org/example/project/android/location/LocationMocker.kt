package org.example.project.android.location

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext
import kotlin.math.*
import kotlin.random.Random

/**
 * Утилита для тестирования геолокации
 * Эмулирует различные сценарии движения пользователя
 */
object LocationMocker {

    private val logger = Logger.withTag("LocationMocker")
    private var mockingJob: Job? = null
    private var currentMockingType: MockingType = MockingType.LINEAR

    enum class MockingType {
        LINEAR,      // Прямолинейное движение к цели
        CURVED,      // Движение по кривой
        RANDOM_WALK, // Случайное блуждание в сторону цели
        CIRCULAR,    // Движение по кругу вокруг цели
        REALISTIC    // Реалистичное движение с остановками
    }

    data class MockingConfig(
        val type: MockingType = MockingType.LINEAR,
        val stepCount: Int = 20,
        val updateIntervalMs: Long = 2000,
        val startDistanceKm: Double = 0.2, // 200м
        val noiseLevel: Double = 0.0001,   // Шум в координатах
        val hasStops: Boolean = false,      // Делать ли остановки
        val stopProbability: Double = 0.1   // Вероятность остановки на каждом шаге
    )

    /**
     * Запустить эмуляцию движения к точке с простой конфигурацией
     */
    fun startMockLocation(
        targetLat: Double,
        targetLon: Double,
        onLocationUpdate: (Double, Double) -> Unit
    ) {
        startMockLocation(
            targetLat = targetLat,
            targetLon = targetLon,
            config = MockingConfig(),
            onLocationUpdate = onLocationUpdate
        )
    }

    /**
     * Запустить эмуляцию движения с расширенной конфигурацией
     */
    fun startMockLocation(
        targetLat: Double,
        targetLon: Double,
        config: MockingConfig,
        onLocationUpdate: (Double, Double) -> Unit
    ) {
        stopMockLocation()
        currentMockingType = config.type

        mockingJob = CoroutineScope(Dispatchers.IO).launch {
            logger.d { "Начинаем эмуляцию ${config.type} движения к точке ($targetLat, $targetLon)" }

            when (config.type) {
                MockingType.LINEAR -> simulateLinearMovement(targetLat, targetLon, config, onLocationUpdate)
                MockingType.CURVED -> simulateCurvedMovement(targetLat, targetLon, config, onLocationUpdate)
                MockingType.RANDOM_WALK -> simulateRandomWalk(targetLat, targetLon, config, onLocationUpdate)
                MockingType.CIRCULAR -> simulateCircularMovement(targetLat, targetLon, config, onLocationUpdate)
                MockingType.REALISTIC -> simulateRealisticMovement(targetLat, targetLon, config, onLocationUpdate)
            }

            logger.d { "Эмуляция ${config.type} завершена" }
        }
    }

    /**
     * Прямолинейное движение к цели
     */
    private suspend fun simulateLinearMovement(
        targetLat: Double,
        targetLon: Double,
        config: MockingConfig,
        onLocationUpdate: (Double, Double) -> Unit
    ) {
        // Начальная точка
        var currentLat = targetLat + config.startDistanceKm * 0.009 // ~1км = 0.009 градуса
        var currentLon = targetLon + config.startDistanceKm * 0.009

        val stepLat = (targetLat - currentLat) / config.stepCount
        val stepLon = (targetLon - currentLon) / config.stepCount

        repeat(config.stepCount + 5) { step ->
            if (!coroutineContext.isActive) return

            currentLat += stepLat
            currentLon += stepLon

            // Добавляем шум
            val noiseLat = (Random.nextDouble() - 0.5) * config.noiseLevel
            val noiseLon = (Random.nextDouble() - 0.5) * config.noiseLevel

            val distance = calculateDistance(currentLat, currentLon, targetLat, targetLon)
            logger.d { "Шаг $step: ($currentLat, $currentLon), дистанция: ${(distance * 1000).toInt()}м" }

            onLocationUpdate(currentLat + noiseLat, currentLon + noiseLon)

            // Остановки
            if (config.hasStops && Random.nextDouble() < config.stopProbability) {
                logger.d { "Остановка на шаге $step" }
                delay(config.updateIntervalMs * 2)
            } else {
                delay(config.updateIntervalMs)
            }
        }
    }

    /**
     * Движение по кривой
     */
    private suspend fun simulateCurvedMovement(
        targetLat: Double,
        targetLon: Double,
        config: MockingConfig,
        onLocationUpdate: (Double, Double) -> Unit
    ) {
        val startLat = targetLat + config.startDistanceKm * 0.009
        val startLon = targetLon - config.startDistanceKm * 0.009 * 0.7 // Начинаем сбоку

        repeat(config.stepCount + 5) { step ->
            if (!coroutineContext.isActive) return

            val progress = step.toDouble() / config.stepCount

            // Параметрическая кривая (квадратичная Безье)
            val controlLat = (startLat + targetLat) / 2 + config.startDistanceKm * 0.005
            val controlLon = (startLon + targetLon) / 2

            val currentLat = (1 - progress).pow(2) * startLat +
                           2 * (1 - progress) * progress * controlLat +
                           progress.pow(2) * targetLat
            val currentLon = (1 - progress).pow(2) * startLon +
                           2 * (1 - progress) * progress * controlLon +
                           progress.pow(2) * targetLon

            // Добавляем шум
            val noiseLat = (Random.nextDouble() - 0.5) * config.noiseLevel
            val noiseLon = (Random.nextDouble() - 0.5) * config.noiseLevel

            val distance = calculateDistance(currentLat, currentLon, targetLat, targetLon)
            logger.d { "Кривой шаг $step: ($currentLat, $currentLon), дистанция: ${(distance * 1000).toInt()}м" }

            onLocationUpdate(currentLat + noiseLat, currentLon + noiseLon)
            delay(config.updateIntervalMs)
        }
    }

    /**
     * Случайное блуждание к цели
     */
    private suspend fun simulateRandomWalk(
        targetLat: Double,
        targetLon: Double,
        config: MockingConfig,
        onLocationUpdate: (Double, Double) -> Unit
    ) {
        var currentLat = targetLat + config.startDistanceKm * 0.009
        var currentLon = targetLon + config.startDistanceKm * 0.009

        repeat(config.stepCount * 2) { step -> // Больше шагов для случайного блуждания
            if (!coroutineContext.isActive) return

            // Направление к цели
            val directionLat = (targetLat - currentLat)
            val directionLon = (targetLon - currentLon)

            // Случайная компонента
            val randomLat = (Random.nextDouble() - 0.5) * config.startDistanceKm * 0.002
            val randomLon = (Random.nextDouble() - 0.5) * config.startDistanceKm * 0.002

            // Смешиваем направленное движение со случайным (70% к цели, 30% случайно)
            currentLat += directionLat * 0.7 / config.stepCount + randomLat
            currentLon += directionLon * 0.7 / config.stepCount + randomLon

            val distance = calculateDistance(currentLat, currentLon, targetLat, targetLon)
            logger.d { "Случайный шаг $step: ($currentLat, $currentLon), дистанция: ${(distance * 1000).toInt()}м" }

            onLocationUpdate(currentLat, currentLon)

            // Если пришли близко к цели, останавливаемся
            if (distance < 0.0001) { // ~10 метров
                logger.d { "Достигли цели при случайном блуждании" }
                return
            }

            delay(config.updateIntervalMs / 2) // Более частые обновления
        }
    }

    /**
     * Движение по кругу вокруг цели
     */
    private suspend fun simulateCircularMovement(
        targetLat: Double,
        targetLon: Double,
        config: MockingConfig,
        onLocationUpdate: (Double, Double) -> Unit
    ) {
        val radius = config.startDistanceKm * 0.009 / 2 // Радиус круга

        repeat(config.stepCount + 10) { step ->
            if (!coroutineContext.isActive) return

            val angle = (step * 2 * PI) / config.stepCount
            val currentLat = targetLat + radius * cos(angle)
            val currentLon = targetLon + radius * sin(angle)

            val distance = calculateDistance(currentLat, currentLon, targetLat, targetLon)
            logger.d { "Круговой шаг $step: ($currentLat, $currentLon), дистанция: ${(distance * 1000).toInt()}м" }

            onLocationUpdate(currentLat, currentLon)
            delay(config.updateIntervalMs)
        }

        // После кругового движения приближаемся к цели
        simulateLinearMovement(
            targetLat, targetLon,
            config.copy(stepCount = 5, startDistanceKm = radius * 111), // Конвертируем обратно в км
            onLocationUpdate
        )
    }

    /**
     * Реалистичное движение с остановками и вариациями скорости
     */
    private suspend fun simulateRealisticMovement(
        targetLat: Double,
        targetLon: Double,
        config: MockingConfig,
        onLocationUpdate: (Double, Double) -> Unit
    ) {
        var currentLat = targetLat + config.startDistanceKm * 0.009
        var currentLon = targetLon + config.startDistanceKm * 0.009

        val totalSteps = config.stepCount + 10
        var stepsTaken = 0

        while (stepsTaken < totalSteps && coroutineContext.isActive) {
            // Направление к цели
            val directionLat = (targetLat - currentLat)
            val directionLon = (targetLon - currentLon)
            val distance = calculateDistance(currentLat, currentLon, targetLat, targetLon)

            // Размер шага зависит от расстояния до цели
            val stepSize = when {
                distance > 0.001 -> 1.0 / totalSteps // Обычный шаг
                distance > 0.0005 -> 0.5 / totalSteps // Медленнее при приближении
                else -> 0.2 / totalSteps // Очень медленно у цели
            }

            currentLat += directionLat * stepSize
            currentLon += directionLon * stepSize

            // Добавляем реалистичный шум (GPS не идеален)
            val gpsNoiseLat = (Random.nextDouble() - 0.5) * 0.00005 // ~5м точность
            val gpsNoiseLon = (Random.nextDouble() - 0.5) * 0.00005

            logger.d { "Реалистичный шаг $stepsTaken: ($currentLat, $currentLon), дистанция: ${(distance * 1000).toInt()}м" }

            onLocationUpdate(currentLat + gpsNoiseLat, currentLon + gpsNoiseLon)

            // Вариативная задержка (имитация разной скорости движения)
            val baseDelay = config.updateIntervalMs
            val variableDelay = when {
                Random.nextDouble() < 0.1 -> baseDelay * 3 // 10% времени - долгая остановка
                Random.nextDouble() < 0.2 -> baseDelay * 2 // 20% времени - короткая остановка
                Random.nextDouble() < 0.3 -> baseDelay / 2 // 30% времени - быстрое движение
                else -> baseDelay // 40% времени - нормальная скорость
            }

            if (variableDelay > baseDelay) {
                logger.d { "Остановка на ${variableDelay}мс" }
            }

            delay(variableDelay)
            stepsTaken++

            // Если достигли цели
            if (distance < 0.0001) {
                logger.d { "Достигли цели при реалистичном движении" }
                break
            }
        }
    }

    /**
     * Рассчитать расстояние между двумя координатами (формула Haversine)
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Радиус Земли в километрах
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    /**
     * Остановить эмуляцию
     */
    fun stopMockLocation() {
        mockingJob?.cancel()
        mockingJob = null
        logger.d { "Эмуляция местоположения остановлена" }
    }

    /**
     * Проверить, активна ли эмуляция
     */
    fun isMocking(): Boolean {
        return mockingJob?.isActive == true
    }

    /**
     * Получить тип текущей эмуляции
     */
    fun getCurrentMockingType(): MockingType? {
        return if (isMocking()) currentMockingType else null
    }

    /**
     * Быстрые методы для запуска различных типов эмуляции
     */
    object QuickStart {
        fun linearToTarget(targetLat: Double, targetLon: Double, onLocationUpdate: (Double, Double) -> Unit) {
            startMockLocation(targetLat, targetLon, MockingConfig(MockingType.LINEAR), onLocationUpdate)
        }

        fun realisticToTarget(targetLat: Double, targetLon: Double, onLocationUpdate: (Double, Double) -> Unit) {
            startMockLocation(
                targetLat, targetLon,
                MockingConfig(MockingType.REALISTIC, hasStops = true, stopProbability = 0.15),
                onLocationUpdate
            )
        }

        fun fastTest(targetLat: Double, targetLon: Double, onLocationUpdate: (Double, Double) -> Unit) {
            startMockLocation(
                targetLat, targetLon,
                MockingConfig(MockingType.LINEAR, stepCount = 10, updateIntervalMs = 1000),
                onLocationUpdate
            )
        }

        fun randomWalkToTarget(targetLat: Double, targetLon: Double, onLocationUpdate: (Double, Double) -> Unit) {
            startMockLocation(
                targetLat, targetLon,
                MockingConfig(MockingType.RANDOM_WALK, stepCount = 15, updateIntervalMs = 1500),
                onLocationUpdate
            )
        }
    }
}
