package org.example.project.ui.screens.tasksScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

import org.example.project.ui.screens.tasksScreen.conditionScreens.MainScreenWithTasks

@Composable
fun TasksScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MainScreenWithTasks(navController = navController)
    }
}
