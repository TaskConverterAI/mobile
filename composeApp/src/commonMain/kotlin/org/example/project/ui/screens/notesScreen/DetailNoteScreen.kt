package org.example.project.ui.screens.notesScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.serialization.Serializable

import org.example.project.data.commonData.Note
import org.example.project.ui.theme.LightGray
import org.example.project.ui.theme.PrimaryBase
import org.example.project.ui.theme.PrimaryDark
import org.example.project.ui.theme.PrimaryLight
import org.example.project.ui.theme.SecondaryBase
import org.example.project.ui.theme.SecondaryDark
import org.example.project.ui.theme.SecondaryLight
import org.example.project.ui.theme.DarkGray

@Serializable
data class DetailNoteScreenArgs(val noteID: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailNoteScreen(note: Note?, navController: NavController) {

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Назад", color = MaterialTheme.colorScheme.primary) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Вернуться назад",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = LightGray
                )
            }
        },
        bottomBar = {
            Column {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* TODO: Implement edit */ },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Редактировать")
                    }
                    OutlinedButton(
                        onClick = { /* TODO: Implement delete */ },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Удалить")
                    }
                }
            }
        }
    ) {
        paddingValues ->
        if (note == null) {
            Text("Упс. Что-то пошло не так...", style =MaterialTheme.typography.headlineLarge)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(note.title, style = MaterialTheme.typography.displayLarge)

                Spacer(modifier = Modifier.height(30.dp))

                Text("Детали заметки", style = MaterialTheme.typography.headlineLarge)

                Spacer(modifier = Modifier.height(15.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Геометка: ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        note.geotag,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Группа: ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        note.group.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Цвет: ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            getColorName(note.color),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = note.color,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(end = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(30.dp))

                Text("Описание", style = MaterialTheme.typography.headlineLarge)

                Spacer(Modifier.height(15.dp))

                Text(note.content, style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(30.dp))

                Text("Комментарии", style = MaterialTheme.typography.headlineLarge)
            }
        }
    }
}

private fun getColorName(color: Color): String {
    return when (color) {
        PrimaryBase -> "Синий"
        PrimaryDark -> "Тёмно-синий"
        PrimaryLight -> "Светло-синий"
        SecondaryBase -> "Жёлтый"
        SecondaryDark -> "Тёмно-жёлтый"
        SecondaryLight -> "Светло-жёлтый"
        DarkGray -> "Тёмно-серый"
        LightGray -> "Светло-серый"
        Color.Red -> "Красный"
        Color.Green -> "Зелёный"
        Color.Blue -> "Синий"
        Color.Yellow -> "Жёлтый"
        Color.Cyan -> "Голубой"
        Color.Magenta -> "Пурпурный"
        Color.White -> "Белый"
        Color.Black -> "Чёрный"
        Color.Gray -> "Серый"
        else -> "Пользовательский"
    }
}
