package org.example.project

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import co.touchlab.kermit.Logger

import org.example.project.ui.TaskConvertAIApp
import org.example.project.ui.theme.TaskConvertAIAppTheme
import org.example.project.android.permissions.LocationPermissionHelper
import org.example.project.android.permissions.NotificationPermissionHelper

class MainActivity : ComponentActivity() {

    private val logger = Logger.withTag("MainActivity")

    // Регистрируем лаунчеры для разрешений
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        logger.d { "Разрешение на уведомления: $isGranted" }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        logger.d { "Разрешения на геолокацию: $granted" }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initializeAndroid(this)
        enableEdgeToEdge()

        // Запрашиваем разрешения после инициализации
        requestPermissionsIfNeeded()

        // Обрабатываем переход к задаче из уведомления
        handleNotificationIntent(intent)

        setContent {
            TaskConvertAIAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TaskConvertAIApp()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Обрабатываем новые интенты (например, при клике на уведомление когда приложение уже открыто)
        handleNotificationIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        // Уведомляем NotificationService что приложение на переднем плане
        updateAppForegroundState(true)
    }

    override fun onPause() {
        super.onPause()
        // Уведомляем NotificationService что приложение в фоне
        updateAppForegroundState(false)
    }

    /**
     * Обработка перехода к задаче из уведомления
     */
    private fun handleNotificationIntent(intent: Intent?) {
        val taskId = intent?.getLongExtra(
            org.example.project.android.notifications.NotificationHelper.EXTRA_TASK_ID,
            -1L
        ) ?: -1L

        if (taskId != -1L) {
            logger.d { "Получен переход к задаче $taskId из уведомления" }
            // TODO: Здесь можно добавить логику навигации к конкретной задаче
            // Например, передать taskId в TaskConvertAIApp через ViewModel или Navigation
        }
    }

    /**
     * Обновляет состояние приложения в NotificationService
     */
    private fun updateAppForegroundState(inForeground: Boolean) {
        try {
            val notificationService = AppDependencies.container.notificationService
            if (notificationService is org.example.project.android.notifications.AndroidNotificationService) {
                notificationService.setAppForegroundState(inForeground)
            }
        } catch (e: Exception) {
            logger.w(e) { "Не удалось обновить состояние приложения: ${e.message}" }
        }
    }

    private fun requestPermissionsIfNeeded() {
        try {
            // Запрашиваем разрешения на уведомления (Android 13+)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (!NotificationPermissionHelper.hasNotificationPermission(this)) {
                    logger.d { "Запрашиваем разрешение на уведомления" }
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            // Запрашиваем разрешения на геолокацию
            if (!LocationPermissionHelper.hasLocationPermission(this)) {
                logger.d { "Запрашиваем разрешения на геолокацию" }
                locationPermissionLauncher.launch(LocationPermissionHelper.getLocationPermissions())
            }
        } catch (e: Exception) {
            logger.e(e) { "Ошибка при запросе разрешений: ${e.message}" }
        }
    }
}
