package org.example.project.ui.screens.statusToast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay

enum class StatusType {
    SUCCESS,  // Успешная операция
    ERROR,    // Ошибка
    WARNING,  // Предупреждение
    INFO      // Информационное сообщение
}

@Composable
fun StatusToast(
    type: StatusType = StatusType.INFO,
    message: String,
    duration: ToastDuration = ToastDuration.SHORT,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(true) }

    // Автоматически скрываем по таймеру и вызываем onDismiss
    LaunchedEffect(visible) {
        if (visible) {
            delay(duration.millis)
            visible = false
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Popup(alignment = Alignment.BottomCenter) {
            Surface(
                modifier = modifier.padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                color = when (type) {
                    StatusType.SUCCESS -> Color(0xFF2E7D32)
                    StatusType.ERROR -> Color(0xFFD32F2F)
                    StatusType.WARNING -> Color(0xFFF57C00)
                    StatusType.INFO -> Color(0xFF1976D2)
                },
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = type.icon,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = message,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

enum class ToastDuration(val millis: Long) {
    SHORT(2000L),
    LONG(4000L)
}

val StatusType.icon
    get() = when (this) {
        StatusType.SUCCESS -> Icons.Filled.CheckCircle
        StatusType.ERROR -> Icons.Filled.Error
        StatusType.WARNING -> Icons.Filled.Warning
        StatusType.INFO -> Icons.Filled.Info
    }