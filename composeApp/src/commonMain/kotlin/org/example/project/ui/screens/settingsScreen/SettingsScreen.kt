package org.example.project.ui.screens.settingsScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.example.project.ui.TaskConvertAIAppScreens
import org.example.project.ui.screens.auth.AuthViewModel


@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val userId by authViewModel.userId.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
               authViewModel.logout(userId = userId)
                navController.navigate(TaskConvertAIAppScreens.SignIn.name)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Выйти")
        }
    }
}
