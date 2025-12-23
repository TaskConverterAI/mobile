package org.example.project.android.location

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import co.touchlab.kermit.Logger
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import org.example.project.MainActivity

class LocationTrackingService : Service() {

    private val logger = Logger.withTag("LocationTrackingService")
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isLocationTracking = false

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "location_tracking"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START_TRACKING = "START_TRACKING"
        const val ACTION_STOP_TRACKING = "STOP_TRACKING"

        var onLocationUpdate: ((Double, Double) -> Unit)? = null

        fun startLocationTracking(context: Context) {
            val intent = Intent(context, LocationTrackingService::class.java).apply {
                action = ACTION_START_TRACKING
            }
            context.startForegroundService(intent)
        }

        fun stopLocationTracking(context: Context) {
            val intent = Intent(context, LocationTrackingService::class.java).apply {
                action = ACTION_STOP_TRACKING
            }
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        logger.d { "LocationTrackingService создан" }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
        setupLocationCallback()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TRACKING -> {
                if (!isLocationTracking) {
                    startForegroundService()
                    startLocationUpdates()
                }
            }
            ACTION_STOP_TRACKING -> {
                stopLocationUpdates()
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.d { "LocationTrackingService уничтожен" }
        stopLocationUpdates()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Отслеживание геопозиции",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Уведомления для отслеживания местоположения для геонапоминаний"
            setShowBadge(false)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createForegroundNotification(): Notification {
        val intent = Intent().apply {
            setClassName(this@LocationTrackingService, "org.example.project.MainActivity")
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Геонапоминания активны")
            .setContentText("Отслеживание местоположения для напоминаний о задачах")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun startForegroundService() {
        val notification = createForegroundNotification()
        startForeground(NOTIFICATION_ID, notification)
        logger.d { "Foreground сервис запущен" }
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                locationResult.lastLocation?.let { location ->
                    logger.d { "Получено местоположение: ${location.latitude}, ${location.longitude}" }

                    // Передаем координаты в NotificationService
                    serviceScope.launch {
                        onLocationUpdate?.invoke(location.latitude, location.longitude)
                    }
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            logger.e { "Нет разрешений на геолокацию" }
            stopSelf()
            return
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000) // 30 секунд
            .setMinUpdateDistanceMeters(50f) // минимум 50 метров
            .setMinUpdateIntervalMillis(15000) // минимум 15 секунд
            .setMaxUpdateDelayMillis(60000) // максимум 60 секунд
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        ).addOnSuccessListener {
            isLocationTracking = true
            logger.d { "Отслеживание геопозиции запущено" }
        }.addOnFailureListener { e ->
            logger.e(e) { "Ошибка при запуске отслеживания: ${e.message}" }
            stopSelf()
        }
    }

    private fun stopLocationUpdates() {
        if (isLocationTracking) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            isLocationTracking = false
            logger.d { "Отслеживание геопозиции остановлено" }
        }
    }
}
