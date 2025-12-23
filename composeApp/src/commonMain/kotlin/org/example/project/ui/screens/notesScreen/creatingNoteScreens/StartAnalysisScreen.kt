package org.example.project.ui.screens.notesScreen.creatingNoteScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import kotlinx.serialization.Serializable

import org.example.project.ui.theme.LightGray
import org.example.project.AppDependencies
import org.example.project.data.geo.GeoTagPreset

@Serializable
data class StartAnalysisScreenArgs(
    val jobId: String,
    val hints: String,
    val text: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun StartAnalysisScreen(navController: NavController, viewModel: StartAnalysisViewModel) {
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
        }
    ) { paddingValues ->
        val title by viewModel.name.collectAsState()
        val location by viewModel.location.collectAsState()
        val date by viewModel.date.collectAsState()

        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()
        val timePickerState = rememberTimePickerState()

        // Обработка результатов от MapPicker
        LaunchedEffect(navController.currentBackStackEntry) {
            val handle = navController.currentBackStackEntry?.savedStateHandle
            val lat: Double? = handle?.get("map_lat")
            val lon: Double? = handle?.get("map_lon")
            val name: String? = handle?.get("map_name")
            val colorLong: Long? = handle?.get("map_color")
            if (lat != null && lon != null) {
                val locationName = name ?: "Выбранная точка"
                viewModel.updateCoords(lat, lon)
                viewModel.updateLocation(locationName)

                // Сохраняем новый геотег как пресет
                val geoRepo = AppDependencies.container.geoTagRepository
                val preset = GeoTagPreset(
                    name = locationName,
                    latitude = lat,
                    longitude = lon,
                    colorValueLong = colorLong
                )
                geoRepo.addPreset(preset)

                handle.remove<Double>("map_lat")
                handle.remove<Double>("map_lon")
                handle.remove<String>("map_name")
                handle.remove<Long>("map_color")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Укажи детали",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(20.dp).scale(1.1F)
                )

                Text(
                    "Название, дата и геометка помогут связать заметку с контекстом",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("Название") },
                    placeholder = { Text("Например: Встреча с командой") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = LightGray
                    ),
                    trailingIcon = {
                        if (title.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateName("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Очистить",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = date,
                    onValueChange = { },
                    label = { Text("Дата и время") },
                    placeholder = { Text("ДД.ММ.ГГГГ ЧЧ:ММ") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable { showDatePicker = true },
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = LightGray,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = LightGray
                    ),
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Выбрать дату и время",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (date.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateDate("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Очистить",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))


                // Геометка (опциональное поле)
                val geoRepo = remember { AppDependencies.container.geoTagRepository }
                var presets by remember { mutableStateOf<List<GeoTagPreset>>(emptyList()) }
                var presetsExpanded by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    geoRepo.presetsFlow().collect { presets = it }
                }

                Box {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { /* read-only via dropdown */ },
                        label = { Text("Геометка (опционально)") },
                        placeholder = { Text("Выберите из списка или создайте на карте") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        readOnly = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = LightGray,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = LightGray
                        ),
                        trailingIcon = {
                            if (location.isNotEmpty()) {
                                IconButton(onClick = {
                                    viewModel.updateLocation("")
                                    viewModel.updateCoords(null, null)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Очистить",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
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
                                    viewModel.updateLocation(preset.name)
                                    viewModel.updateCoords(preset.latitude, preset.longitude)
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

                Spacer(modifier = Modifier.height(32.dp))
            }


            Button(
                onClick = {
                    viewModel.startAnalysis()
                    navController.navigate("tasks")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .height(56.dp),
                enabled = title.isNotEmpty()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Транскрибировать",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }


            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    showDatePicker = false
                                    showTimePicker = true
                                } ?: run {
                                    showDatePicker = false
                                }
                            }
                        ) {
                            Text("Далее")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDatePicker = false }) {
                            Text("Отмена")
                        }
                    },
                    colors = DatePickerDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    DatePicker(
                        state = datePickerState, colors = DatePickerDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            if (showTimePicker) {
                TimePickerDialog(
                    title = { Text("Выберите время") },
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val selectedDate = kotlin.time.Instant.fromEpochMilliseconds(millis)
                                    val localDate = selectedDate.toLocalDateTime(TimeZone.currentSystemDefault()).date
                                    val hour = timePickerState.hour
                                    val minute = timePickerState.minute
                                    val dateTime = "${
                                        localDate.day.toString().padStart(2, '0')
                                    }.${
                                        localDate.month.number.toString().padStart(2, '0')
                                    }.${localDate.year} ${
                                        hour.toString().padStart(2, '0')
                                    }:${
                                        minute.toString().padStart(2, '0')
                                    }"
                                    viewModel.updateDate(dateTime)
                                }
                                showTimePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showTimePicker = false }) {
                            Text("Отмена")
                        }
                    }
                ) {
                    TimePicker(state = timePickerState)
                }
            }
        }
    }
}

private fun pastelColorForKey(key: String): androidx.compose.ui.graphics.Color {
    val colors = listOf(
        androidx.compose.ui.graphics.Color(0xFFFFCDD2), // Light Red
        androidx.compose.ui.graphics.Color(0xFFC8E6C9), // Light Green
        androidx.compose.ui.graphics.Color(0xFFBBDEFB), // Light Blue
        androidx.compose.ui.graphics.Color(0xFFF8BBD9), // Light Pink
        androidx.compose.ui.graphics.Color(0xFFE1BEE7), // Light Purple
        androidx.compose.ui.graphics.Color(0xFFFFF9C4), // Light Yellow
        androidx.compose.ui.graphics.Color(0xFFFFE0B2), // Light Orange
        androidx.compose.ui.graphics.Color(0xFFD7CCC8), // Light Brown
        androidx.compose.ui.graphics.Color(0xFFB2DFDB), // Light Teal
        androidx.compose.ui.graphics.Color(0xFFF0F4C3)  // Light Lime
    )
    return colors[key.hashCode().let { if (it < 0) -it else it } % colors.size]
}

