package org.example.project.ui.screens.groupsScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

import org.example.project.ui.screens.groupsScreen.conditionScreens.EmptyScreen
import org.example.project.ui.screens.groupsScreen.states.GroupsViewModel
import org.example.project.ui.screens.groupsScreen.states.MainScreenWithGroups

@Composable
fun GroupsScreen(navController: NavController, viewModel: GroupsViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val listState by viewModel.listUi.collectAsState()

        if (listState.isEmptyList) {
            EmptyScreen()
        } else {
            MainScreenWithGroups(navController, viewModel)
        }

    }
}
