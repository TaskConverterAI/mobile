package org.example.project.android.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger

/**
 * Утилита для работы с разрешениями на геолокацию
 */
object LocationPermissionHelper {

    private val logger = Logger.withTag("LocationPermissionHelper")

    /**
     * Проверить, есть ли разрешения на грубое местоположение
     */
    fun hasCoarseLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Проверить, есть ли разрешения на точное местоположение
     */
    fun hasFineLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Проверить, есть ли хотя бы одно разрешение на местоположение
     */
    fun hasLocationPermission(context: Context): Boolean {
        return hasCoarseLocationPermission(context) || hasFineLocationPermission(context)
    }

    /**
     * Получить массив отсутствующих разрешений
     */
    fun getMissingLocationPermissions(context: Context): Array<String> {
        val missingPermissions = mutableListOf<String>()

        if (!hasCoarseLocationPermission(context)) {
            missingPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (!hasFineLocationPermission(context)) {
            missingPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        return missingPermissions.toTypedArray()
    }

    /**
     * Получить все необходимые разрешения на местоположение
     */
    fun getLocationPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}
