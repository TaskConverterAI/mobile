package org.example.project.ui.screens.settingsScreen

/**
 * Android реализация демо уведомлений
 */
actual suspend fun runNotificationDemo() {
    try {
        org.example.project.demo.NotificationDemo.runNotificationDemo(
            org.example.project.demo.NotificationDemo.DemoType.QUICK,
            org.example.project.demo.NotificationDemo.TestLocation.RED_SQUARE
        )
    } catch (e: Exception) {
        println("Ошибка при запуске быстрого демо: ${e.message}")
    }
}

actual suspend fun runRealisticNotificationDemo() {
    try {
        org.example.project.demo.NotificationDemo.runNotificationDemo(
            org.example.project.demo.NotificationDemo.DemoType.REALISTIC,
            org.example.project.demo.NotificationDemo.TestLocation.BOLSHOI_THEATRE
        )
    } catch (e: Exception) {
        println("Ошибка при запуске реалистичного демо: ${e.message}")
    }
}

actual suspend fun runShowcaseNotificationDemo() {
    try {
        org.example.project.demo.NotificationDemo.runNotificationDemo(
            org.example.project.demo.NotificationDemo.DemoType.SHOWCASE,
            org.example.project.demo.NotificationDemo.TestLocation.GORKY_PARK
        )
    } catch (e: Exception) {
        println("Ошибка при запуске showcase демо: ${e.message}")
    }
}

actual suspend fun stopNotificationDemo() {
    try {
        org.example.project.demo.NotificationDemo.stopNotificationDemo()
    } catch (e: Exception) {
        println("Ошибка при остановке демо: ${e.message}")
    }
}
