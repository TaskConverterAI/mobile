package org.example.project.ui.screens.settingsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.example.project.AppDependencies
import org.example.project.ui.TaskConvertAIAppScreens
import org.example.project.ui.screens.auth.AuthViewModel


@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val userId by authViewModel.userId.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val scope = rememberCoroutineScope()

    // Получаем NotificationService
    val notificationService = try {
        AppDependencies.container.notificationService
    } catch (_: Exception) {
        null
    }

    // Загружаем данные пользователя при первом отображении экрана
    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Аватар пользователя
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Аватар пользователя",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Имя пользователя
        Text(
            text = currentUser?.username ?: "Загрузка...",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email пользователя
        Text(
            text = currentUser?.email ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Неактивная кнопка "Выбрать нейросеть"
        Button(
            onClick = { /* Будет реализовано позже */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            enabled = false
        ) {
            Text("Выбрать нейросеть (Скоро...)")
        }



        Spacer(modifier = Modifier.height(32.dp))

        // Кнопка реалистичного демо
        Button(
            onClick = {
                scope.launch {
                    try {
                        runRealisticNotificationDemo()
                    } catch (e: Exception) {
                        println("Ошибка при запуске реалистичного демо: ${e.message}")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Реалистичное движение")
        }

        // Кнопка остановки демо
        Button(
            onClick = {
                scope.launch {
                    try {
                        stopNotificationDemo()
                    } catch (e: Exception) {
                        println("Ошибка при остановке демо: ${e.message}")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text("Остановить демо")
        }

        // Кнопка выхода
        Button(
            onClick = {
                authViewModel.logout(userId = userId)
                navController.navigate(TaskConvertAIAppScreens.SignIn.name)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Выйти")
        }
    }
}
