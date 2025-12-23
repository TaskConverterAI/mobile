package org.example.project.ui.screens.settingsScreen

/**
 * Платформоспецифичные функции для запуска демо уведомлений
 */
expect suspend fun runNotificationDemo()

expect suspend fun runRealisticNotificationDemo()

expect suspend fun runShowcaseNotificationDemo()

expect suspend fun stopNotificationDemo()
