package org.example.project.ui.permissions

import androidx.compose.runtime.Composable

/**
 * iOS реализация обработчика разрешений (заглушка)
 */
@Composable
actual fun PlatformPermissionHandler(
    onPermissionsGranted: () -> Unit
) {
    // На iOS пока ничего не делаем
}
