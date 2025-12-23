package org.example.project.ui.permissions

import androidx.compose.runtime.Composable

/**
 * Композабл для платформоспецифичной обработки разрешений
 */
@Composable
expect fun PlatformPermissionHandler(
    onPermissionsGranted: () -> Unit = {}
)
