package org.example.project.ui.screens.notesScreen
import CommentDialog
import kotlinx.datetime.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.math.absoluteValue

import org.example.project.AppDependencies
import org.example.project.data.commonData.Comment
import org.example.project.data.commonData.Group
import org.example.project.data.commonData.Location
import org.example.project.data.commonData.Note
import org.example.project.data.commonData.User
import org.example.project.data.geo.GeoTagPreset
import org.example.project.ui.screens.commentElems.CommentList
import org.example.project.ui.viewComponents.commonComponents.NotifyItem
import org.example.project.ui.theme.LightGray
import org.example.project.ui.theme.PrimaryBase
import org.example.project.ui.theme.PrimaryDark
import org.example.project.ui.theme.PrimaryLight
import org.example.project.ui.theme.SecondaryBase
import org.example.project.ui.theme.SecondaryDark
import org.example.project.ui.theme.SecondaryLight
import org.example.project.ui.theme.DarkGray
import kotlin.time.Instant

@Serializable
data class DetailNoteScreenArgs(val noteID: Int?, val isEditMode: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DetailNoteScreen(
    note: Note?,
    navController: NavController,
    userId: Long,
    group: Group?,
    isEditMode: Boolean = false,
    availableGroups: List<Group> = emptyList(), // Передайте список доступных групп
    onSave: (Note) -> Unit = {}, // Callback для сохранения
    onDelete: (Note) -> Unit = {}, // Callback для удаления,
    onSaveComment: (Comment) -> Unit = {},
    onDeleteComment: (Long) -> Unit = {}
) {
    // Default group для новых заметок
    val defaultGroup = remember {
        Group(
            id = -1L,
            name = "Без группы",
            description = "",
            ownerId = 0L,
            memberCount = 0,
            members = mutableListOf(),
            createdAt = Clock.System.now().toEpochMilliseconds(),
            taskCount = 0
        )
    }
    val _availableGroups = listOf<Group>(defaultGroup).plus(availableGroups)
    // Состояния для редактируемых полей
    var editableTitle by remember { mutableStateOf("") }
    var editableContent by remember { mutableStateOf("") }
    // Состояние название тэга
    var editableGeotag: String? by remember { mutableStateOf("") }
    // Новые состояния для координат
    var editableLat by remember { mutableStateOf<Double?>(null) }
    var editableLon by remember { mutableStateOf<Double?>(null) }
    var editableGroup: Group? by remember { mutableStateOf(defaultGroup) }
    var editableColor by remember { mutableStateOf(PrimaryBase) }

    // Состояния для обработки ошибок
    var showErrorNotification by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    // Инициализируем isNewNote на основе параметра isEditMode, если он true и note == null
    val isNewNote = remember(note, isEditMode) { isEditMode && note == null }
    var isInEditMode by remember { mutableStateOf(isEditMode) }

    LaunchedEffect(note) {

        if (note != null) {
            editableTitle = note.title
            editableContent = note.content
            editableGeotag = note.geotag?.name ?: ""
            editableGroup = group ?: defaultGroup
            editableColor = note.color
            // Инициализируем координаты из существующей заметки
            editableLat = note.geotag?.latitude
            editableLon = note.geotag?.longitude
        }
    }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
            handle.getStateFlow("map_lat", editableLat).collect { editableLat = it }
        }
        navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
            handle.getStateFlow("map_lon", editableLon).collect { editableLon = it }
        }
        navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
            handle.getStateFlow("map_name", editableGeotag).collect { editableGeotag = it }
        }
    }

    // Одноразовое чтение результата при возврате с карты: сразу вставляем в поле "Геометка"
    LaunchedEffect(navController.currentBackStackEntry) {
        val handle = navController.currentBackStackEntry?.savedStateHandle
        val lat: Double? = handle?.get("map_lat")
        val lon: Double? = handle?.get("map_lon")
        val name: String? = handle?.get("map_name")
        val colorLong: Long? = handle?.get("map_color")
        if (lat != null && lon != null) {
            editableLat = lat
            editableLon = lon
            editableGeotag = name ?: "${lat.formatLatLon()}, ${lon.formatLatLon()}"
            handle.remove<Double>("map_lat")
            handle.remove<Double>("map_lon")
            handle.remove<String>("map_name")
            handle.remove<Long>("map_color")
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            if (isNewNote) "Новая заметка"
                            else if (isInEditMode) "Редактирование"
                            else "Назад",
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
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
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isInEditMode) {
                        Button(
                            onClick = {
                                if (isSaving) return@Button

                                isSaving = true
                                try {
                                    @OptIn(ExperimentalTime::class)
                                    val updatedNote = Note(
                                        id = note?.id ?: 0,
                                        title = editableTitle,
                                        content = editableContent,
                                        geotag = Location(
                                            editableLat ?: 0.0,
                                            editableLon ?: 0.0,
                                            editableGeotag ?: "",
                                            false
                                        ),
                                        groupId = if (editableGroup?.id != -1L) editableGroup?.id else null,
                                        comments = note?.comments ?: emptyList(),
                                        color = editableColor,
                                        creationDate = note?.creationDate ?: Clock.System.now().toEpochMilliseconds(),
                                        authorId = userId
                                    )

                                    Logger.i("DetailNoteScreen") {
                                        "Попытка сохранить заметку:\n" +
                                        "  title='${updatedNote.title}' (length=${updatedNote.title.length})\n" +
                                        "  content length=${updatedNote.content.length}\n" +
                                        "  groupId=${updatedNote.groupId}\n" +
                                        "  isNew=$isNewNote\n" +
                                        "  lat=${editableLat}, lon=${editableLon}"
                                    }

                                    onSave(updatedNote)

                                    Logger.i("DetailNoteScreen") { "Заметка успешно сохранена" }

                                    if (!isNewNote) {
                                        isInEditMode = false
                                    } else {
                                        navController.popBackStack()
                                    }
                                } catch (e: Exception) {
                                    Logger.e("DetailNoteScreen", e) {
                                        "Ошибка при сохранении заметки:\n" +
                                        "  message=${e.message}\n" +
                                        "  cause=${e.cause?.message}\n" +
                                        "  stacktrace=${e.stackTraceToString()}"
                                    }

                                    // Формируем понятное сообщение об ошибке
                                    val userMessage = when {
                                        e.message?.contains("400") == true ->
                                            "Ошибка валидации данных. Проверьте заполнение всех полей."
                                        e.message?.contains("Network") == true || e.message?.contains("timeout") == true ->
                                            "Ошибка сети. Проверьте подключение к интернету."
                                        editableContent.length > 10000 ->
                                            "Текст заметки слишком большой (${editableContent.length} символов). Сократите текст."
                                        else ->
                                            e.message ?: "Неизвестная ошибка при сохранении"
                                    }

                                    errorMessage = userMessage
                                    showErrorNotification = true
                                } finally {
                                    isSaving = false
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            enabled = editableTitle.isNotBlank() && editableLat != null && editableLon != null && !isSaving
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isNewNote) "Создать" else "Сохранить")
                        }

                        OutlinedButton(
                            onClick = {
                                if (isNewNote) {
                                    navController.popBackStack()
                                } else if (note != null) {
                                    // Сброс изменений
                                    editableTitle = note.title
                                    editableContent = note.content
                                    editableGeotag = note.geotag?.name
                                    editableGroup = group
                                    editableColor = note.color
                                    isInEditMode = false
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text("Отмена")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { isInEditMode = true },
                            modifier = Modifier.weight(1f),
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
                            onClick = {
                                note?.let { onDelete(it) }
                                navController.popBackStack()
                            },
                            modifier = Modifier.weight(1f),
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
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            // Заголовок
            if (isInEditMode) {
                OutlinedTextField(
                    value = editableTitle,
                    onValueChange = { editableTitle = it },
                    label = { Text("Название заметки") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.displayLarge,
                    singleLine = true
                )
            } else {
                Text(editableTitle, style = MaterialTheme.typography.displayLarge)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text("Детали заметки", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(15.dp))

            // Геометка
            if (isInEditMode) {
                val geoRepo = remember { AppDependencies.container.geoTagRepository }
                var presets by remember { mutableStateOf<List<GeoTagPreset>>(emptyList()) }
                var presetsExpanded by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    geoRepo.presetsFlow().collect { presets = it }
                }

                Box {
                    OutlinedTextField(
                        value = editableGeotag ?: "",
                        onValueChange = { /* read-only via history or map */ },
                        label = { Text("Геометка (тег)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        readOnly = true
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { presetsExpanded = true }
                    )

                    DropdownMenu(
                        expanded = presetsExpanded,
                        onDismissRequest = { presetsExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        presets.forEach { preset ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier.size(16.dp).background(
                                                pastelColorForKey(preset.name),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = preset.name,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                onClick = {
                                    editableLat = preset.latitude
                                    editableLon = preset.longitude
                                    editableGeotag = preset.name
                                    presetsExpanded = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Создать новый на карте") },
                            onClick = {
                                presetsExpanded = false
                                navController.navigate("map_picker")
                            },
                            colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (editableLat != null && editableLon != null) {
                    Text(
                        text = "${editableLat?.formatLatLon()}, ${editableLon?.formatLatLon()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        "Координаты не выбраны",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Геометка: ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    editableGeotag?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (editableLat != null && editableLon != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Координаты: ",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "${editableLat?.formatLatLon()}, ${editableLon?.formatLatLon()}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Группа
            if (isInEditMode) {
                var groupExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = groupExpanded,
                    onExpandedChange = { groupExpanded = !groupExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = editableGroup?.name ?: "Без группы",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        label = { Text("Группа") }
                    )

                    ExposedDropdownMenu(
                        expanded = groupExpanded,
                        onDismissRequest = { groupExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        _availableGroups.forEach {  group ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = group.name,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    editableGroup = group
                                    groupExpanded = false
                                },
                                colors = androidx.compose.material3.MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Группа: ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        editableGroup?.name ?: "None",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }


            Spacer(Modifier.height(30.dp))

            Text("Описание", style = MaterialTheme.typography.headlineLarge)

            Spacer(Modifier.height(15.dp))

            // Содержимое
            if (isInEditMode) {
                OutlinedTextField(
                    value = editableContent,
                    onValueChange = { editableContent = it },
                    label = { Text("Содержимое заметки") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    maxLines = 10
                )
            } else {
                Text(editableContent, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    // Показываем уведомление об ошибке
    if (showErrorNotification) {
        NotifyItem(
            objectName = "Ошибка сохранения заметки",
            objectDescription = errorMessage,
            onDismiss = {
                showErrorNotification = false
                errorMessage = ""
            }
        )
    }
}

internal fun getColorName(color: Color): String {
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

private fun pastelColorForKey(key: String): Color {
    val palette = listOf(
        Color(0xFFE3F2FD), // пастельно-голубой
        Color(0xFFFFF9C4), // пастельно-жёлтый
        Color(0xFFFCE4EC), // пастельно-розовый
        Color(0xFFE8F5E9), // пастельно-зелёный
        Color(0xFFFFF3E0), // пастельно-персиковый
        Color(0xFFEDE7F6), // пастельно-фиолетовый
        Color(0xFFE0F2F1), // пастельно-бирюзовый
        Color(0xFFF3E5F5)  // пастельно-сиреневый
    )
    val idx = (key.hashCode().absoluteValue) % palette.size
    return palette[idx]
}

private fun Double.formatLatLon(): String = this.toString()
