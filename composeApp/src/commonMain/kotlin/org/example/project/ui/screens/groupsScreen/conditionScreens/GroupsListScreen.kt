package org.example.project.ui.screens.groupsScreen.states

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.example.project.ui.theme.TaskConvertAIAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GroupsScreen(
    navController: NavController,
    viewModel: GroupsViewModel = viewModel()
) {
    val groups by viewModel.groups.collectAsState()

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
                GroupCard(
                    group = group,
                    onClick = {
                        viewModel.onGroupClick(group.id)
                        navController.navigate("group_details/${group.id}") // 🔹 заглушка
                    }
                )
            }
        }
    }
}


@Composable
fun GroupCard(
    group: GroupItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }  // 🔹 клик
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {

        Text(
            text = group.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Text(
            text = group.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "members",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = group.membersCount.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "notes",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = group.notesCount.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .padding(top = 12.dp)
                .background(group.bottomBorderColor, shape = RoundedCornerShape(3.dp))
        )
    }
}



@Preview(showBackground = true)
@Composable
fun GroupsScreenPreviewSimple() {
    TaskConvertAIAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Группы",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(bottom = 5.dp, start = 10.dp).scale(1.1f)
            )

            val sampleGroups = listOf(
                GroupItem(
                    id = 1,
                    name = "Product Squad",
                    description = "Планирование, приоритезация задач и контроль пользовательского опыта",
                    membersCount = 3,
                    notesCount = 5,
                    bottomBorderColor = Color(0xFF3D5AFE)
                ),
                GroupItem(
                    id = 2,
                    name = "Project Orbit",
                    description = "Группа для обсуждения продуктовых решений, задач и пользовательских сценариев",
                    membersCount = 10,
                    notesCount = 15,
                    bottomBorderColor = Color(0xFF3D5AFE)
                ),
                GroupItem(
                    id = 3,
                    name = "Frontend Crew",
                    description = "Разработка интерфейсов, UI-компонентов и интеграция с API",
                    membersCount = 4,
                    notesCount = 7,
                    bottomBorderColor = Color(0xFF3D5AFE)
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sampleGroups.forEach { group ->
                    GroupCard(
                        group = group,
                        onClick = {} // клики для превью не нужны
                    )
                }
            }
        }
    }
}

