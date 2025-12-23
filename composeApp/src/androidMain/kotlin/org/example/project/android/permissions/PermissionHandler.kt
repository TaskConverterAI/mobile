package org.example.project.android.permissions

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import co.touchlab.kermit.Logger

/**
 * Композабл для запроса разрешений на уведомления и геолокацию
 */
@Composable
fun PermissionHandler(
    onPermissionsGranted: () -> Unit = {}
) {
    val context = LocalContext.current
    val logger = Logger.withTag("PermissionHandler")

    // Проверяем, что контекст - это ComponentActivity и она в подходящем состоянии
    val activity = context as? ComponentActivity

    if (activity == null) {
        logger.w { "Контекст не является ComponentActivity" }
        return
    }

    // Проверяем состояние lifecycle перед регистрацией
    val canRegister = remember(activity) {
        activity.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED) &&
        !activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    // Лаунчеры создаем только если можем безопасно зарегистрироваться
    val notificationPermissionLauncher = remember(activity) {
        if (canRegister) {
            try {
                activity.registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    logger.d { "Разрешение на уведомления: $isGranted" }
                }
            } catch (e: Exception) {
                logger.e(e) { "Ошибка регистрации лаунчера уведомлений: ${e.message}" }
                null
            }
        } else {
            logger.w { "Не можем зарегистрировать лаунчер уведомлений - неподходящее состояние lifecycle" }
            null
        }
    }

    val locationPermissionLauncher = remember(activity) {
        if (canRegister) {
            try {
                activity.registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val granted = permissions.values.all { it }
                    logger.d { "Разрешения на геолокацию: $granted" }
                    if (granted) {
                        onPermissionsGranted()
                    }
                }
            } catch (e: Exception) {
                logger.e(e) { "Ошибка регистрации лаунчера геолокации: ${e.message}" }
                null
            }
        } else {
            logger.w { "Не можем зарегистрировать лаунчер геолокации - неподходящее состояние lifecycle" }
            null
        }
    }

    LaunchedEffect(Unit) {
        // Запрашиваем разрешения только если лаунчеры успешно созданы
        if (notificationPermissionLauncher != null && locationPermissionLauncher != null) {
            requestPermissions(
                activity,
                notificationPermissionLauncher,
                locationPermissionLauncher
            )
        } else {
            logger.w { "Пропускаем запрос разрешений - лаунчеры не созданы" }
        }
    }
}

private fun requestPermissions(
    context: ComponentActivity,
    notificationLauncher: ActivityResultLauncher<String>,
    locationLauncher: ActivityResultLauncher<Array<String>>
) {
    val logger = Logger.withTag("PermissionHandler")

    try {
        // Проверяем и запрашиваем разрешения на уведомления
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (!org.example.project.android.permissions.NotificationPermissionHelper.hasNotificationPermission(context)) {
                logger.d { "Запрашиваем разрешение на уведомления" }
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Проверяем и запрашиваем разрешения на геолокацию
        if (!LocationPermissionHelper.hasLocationPermission(context)) {
            logger.d { "Запрашиваем разрешения на геолокацию" }
            locationLauncher.launch(LocationPermissionHelper.getLocationPermissions())
        }
    } catch (e: Exception) {
        logger.e(e) { "Ошибка при запросе разрешений: ${e.message}" }
    }
}
