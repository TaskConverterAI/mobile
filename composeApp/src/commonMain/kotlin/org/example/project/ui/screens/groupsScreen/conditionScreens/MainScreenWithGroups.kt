package org.example.project.ui.screens.groupsScreen.conditionScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.example.project.ui.viewComponents.commonComponents.BlockType
import org.example.project.ui.viewComponents.commonComponents.ColorBlock
import org.example.project.ui.viewComponents.commonComponents.NotifyItem

@Composable
fun MainScreenWithGroups(
    navController: NavController,
    viewModel: GroupsViewModel = viewModel()
) {
    val groups by viewModel.groups.collectAsState()

    val listUi by viewModel.listUi.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp)
    ) {
        Text(
            text = "Группы",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 5.dp, start = 10.dp).scale(1.1f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            groups.forEach { group ->
                ColorBlock(BlockType.GROUP, group = group, backgroundColor = MaterialTheme.colorScheme.primary, navController = navController)
            }
        }
    }

    if (listUi.showBottom) {
        NotifyItem(
            objectName = "Группа успешно создана",
            objectDescription = "Состав всегда можно изменить на странице группы",
            onDismiss = { viewModel.setBottom(false) }
        )
    }
}

