package org.example.project.android.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Утилита для работы с разрешениями на уведомления
 */
object NotificationPermissionHelper {

    /**
     * Проверить, есть ли разрешения на показ уведомлений
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // До Android 13 разрешения на уведомления не требовались
        }
    }
}
