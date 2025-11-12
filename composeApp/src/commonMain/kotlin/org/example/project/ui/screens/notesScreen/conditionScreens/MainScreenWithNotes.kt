package org.example.project.ui.screens.notesScreen.conditionScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import org.example.project.data.commonData.Note
import org.example.project.ui.viewComponents.commonComponents.BlockType
import org.example.project.ui.viewComponents.commonComponents.ColorBlock
import org.example.project.ui.viewComponents.commonComponents.FilterSelector
import org.example.project.ui.viewComponents.commonComponents.NotifyItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithNotes(navController: NavController) {
    var selectedFilter by remember { mutableStateOf("Все группы") }
    val filterOptions = listOf("Все группы")

    var showBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp)
    ) {
        Text(
            text = "Заметки",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 5.dp, start = 10.dp).scale(1.1F)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Фильтр:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            )

            FilterSelector(
                selectedFilter = selectedFilter,
                filterOptions = filterOptions,
                onFilterSelected = { selectedFilter = it },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { showBottomSheet = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Test Notify")
        }

        val notes = getSampleNotes(selectedFilter)

        val leftColumnNotes = mutableListOf<Note>()
        val rightColumnNotes = mutableListOf<Note>()

        notes.forEachIndexed { index, note ->
            if (index % 2 == 0) {
                leftColumnNotes.add(note)
            } else {
                rightColumnNotes.add(note)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                leftColumnNotes.forEach { note ->
//                    NoteItem(note = note)
                    ColorBlock(blockType = BlockType.ADVANCED_NOTE, note = note, backgroundColor = note.color, navController = navController)
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rightColumnNotes.forEach { note ->
//                    NoteItem(note = note)
                    ColorBlock(blockType = BlockType.ADVANCED_NOTE, note = note, backgroundColor = note.color, navController = navController)
                }
            }
        }
    }

    if (showBottomSheet) {
        NotifyItem(
            objectName = "Заметка успешно создана",
            objectDescription = "Извлечённые задачи можешь найти в разделе задач",
            onDismiss = { showBottomSheet = false }
        )
    }
}

fun getSampleNotes(filter: String): List<Note> {
    val allNotes = listOf(
        Note(
            title = "Встреча с командой",
            content = "Обсудить планы на следующую неделю",
            geotag = "Офис",
            group = "Работа",
            comments = emptyList(),
            color = Color.Green,
            contentMaxLines = 2
        ),
        Note(
            title = "Список покупок",
            content = "Молоко, хлеб, яйца, масло, сыр, колбаса, овощи и фрукты для недели",
            geotag = "Супермаркет",
            group = "Личные",
            comments = emptyList(),
            color = Color.Cyan,
            contentMaxLines = 5
        ),
        Note(
            title = "Идея для проекта",
            content = "Реализовать новую функцию в приложении с использованием современных подходов",
            geotag = "Дом",
            group = "Важные",
            comments = emptyList(),
            color = Color.Magenta,
            contentMaxLines = 3
        ),
        Note(
            title = "Задача на день",
            content = "Закончить отчёт",
            geotag = "Офис",
            group = "Работа",
            comments = emptyList(),
            color = Color.Yellow,
            contentMaxLines = 1
        ),
        Note(
            title = "Напоминание",
            content = "Позвонить врачу и записаться на приём. Не забыть взять медицинскую карту и результаты анализов",
            geotag = "Поликлиника",
            group = "Важные",
            comments = emptyList(),
            contentMaxLines = 4
        ),
        Note(
            title = "Заметка",
            content = "Короткий текст",
            geotag = "",
            group = "Личные",
            comments = emptyList(),
            color = Color.LightGray,
            contentMaxLines = 1
        ),
        Note(
            title = "План путешествия",
            content = "Забронировать отель, купить билеты на самолёт, составить маршрут по городу, проверить погоду и упаковать чемодан",
            geotag = "Париж",
            group = "Личные",
            comments = emptyList(),
            color = Color(0xFF8A2BE2),
            contentMaxLines = 6
        ),
        Note(
            title = "Рабочие задачи",
            content = "Просмотреть код коллег и оставить комментарии",
            geotag = "Офис",
            group = "Работа",
            comments = emptyList(),
            color = Color(0xFFFFA500),
            contentMaxLines = 2
        ),
        Note(
            title = "Важное сообщение",
            content = "Не забыть отправить отчёт начальнику до конца дня",
            geotag = "Офис",
            group = "Важные",
            comments = emptyList(),
            color = Color.Red,
            contentMaxLines = 2
        ),
        Note(
            title = "Личное развитие",
            content = "Прочитать главу из книги по саморазвитию и сделать заметки",
            geotag = "Библиотека",
            group = "Личные",
            comments = emptyList(),
            color = Color.Blue,
            contentMaxLines = 3
        )
    )

    return if (filter == "Все группы") {
        allNotes
    } else {
        allNotes.filter { it.group == filter }
    }
}
