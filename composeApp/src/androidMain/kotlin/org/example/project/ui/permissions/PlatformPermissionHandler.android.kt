package org.example.project.ui.permissions

import androidx.compose.runtime.Composable
import co.touchlab.kermit.Logger

/**
 * Android реализация обработчика разрешений
 * Перенесено в MainActivity для избежания проблем с lifecycle
 */
@Composable
actual fun PlatformPermissionHandler(
    onPermissionsGranted: () -> Unit
) {
    val logger = Logger.withTag("PlatformPermissionHandler")

    // Больше ничего не делаем в Compose
    // Разрешения должны запрашиваться в MainActivity.onCreate()
    logger.d { "PlatformPermissionHandler вызван, но разрешения запрашиваются в MainActivity" }
}
