package org.example.project.ui.screens.tasksScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.material3.rememberDatePickerState
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
import kotlinx.serialization.Serializable
import org.example.project.data.commonData.Deadline
import kotlin.time.ExperimentalTime

import org.example.project.AppDependencies
import org.example.project.data.commonData.Group
import org.example.project.data.commonData.Location
import org.example.project.data.commonData.Priority
import org.example.project.data.commonData.Status
import org.example.project.data.commonData.Task
import org.example.project.data.commonData.User
import org.example.project.data.geo.GeoTagPreset
import org.example.project.ui.theme.LightGray
import org.example.project.ui.viewComponents.commonComponents.formatDate
import org.example.project.ui.viewComponents.taskScreenComponents.HighPriority
import org.example.project.ui.viewComponents.taskScreenComponents.LowPriority
import org.example.project.ui.viewComponents.taskScreenComponents.MediumPriority
import org.example.project.ui.viewComponents.taskScreenComponents.ToDoStatus
import org.example.project.ui.viewComponents.taskScreenComponents.DoneStatus
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toInstant
import kotlin.math.absoluteValue
import kotlin.math.absoluteValue

@Serializable
data class DetailTaskScreenArgs(val taskID: Long?, val isEditMode: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DetailTaskScreen(
    task: Task?,
    navController: NavController,
    isEditMode: Boolean = false,
    availableGroups: List<Group> = emptyList(),
    availableUsers: HashMap<Long, List<User>> = hashMapOf(),
    onSave: (Task) -> Unit = {},
    onDelete: (Task) -> Unit = {},
    userId: Long
) {
    // Default values for new tasks
    val defaultGroup = remember {
       Group(
            id = -1,
            name = "Без группы",
            description = "",
            ownerId = 0,
            memberCount = 0,
            members = mutableListOf(),
            createdAt = 0,
            taskCount = 0
        )
    }


    val defaultUser = remember {
        User(
            id = userId,
            email = "Я",
            username = "Не назначен",
            privileges = org.example.project.data.commonData.Privileges.admin
        )
    }
    val _availableGroups = remember(availableGroups) { listOf<Group>(defaultGroup).plus(availableGroups) }
    availableUsers.put(defaultGroup.id, listOf(defaultUser))

    // Editable state variables
    var editableTitle by remember { mutableStateOf("") }
    var editableDescription by remember { mutableStateOf("") }
    var editableGeotag by remember { mutableStateOf("") }
    // Новые состояния для координат
    var editableLat by remember { mutableStateOf<Double?>(null) }
    var editableLon by remember { mutableStateOf<Double?>(null) }
    var editableGroup by remember { mutableStateOf(defaultGroup) }
    // Состояния для напоминаний
    var remindByTime by remember { mutableStateOf(false) }
    var remindByLocation by remember { mutableStateOf(false) }
    //var editableAssignee by remember { mutableStateOf(defaultUser) }
    @OptIn(ExperimentalTime::class)
    var editableDueDate by remember { mutableStateOf(kotlin.time.Clock.System.now().toEpochMilliseconds()) }
    var editablePriority by remember { mutableStateOf(Priority.MIDDLE) }
    var editableStatus by remember { mutableStateOf(Status.UNDONE) }

    // Состояние для списка пользователей текущей группы
    var currentUsers by remember { mutableStateOf<List<User>>(emptyList()) }

    var editableAssignee by remember { mutableStateOf(defaultUser) }

    // Обновляем список пользователей при изменении availableUsers
    LaunchedEffect(availableUsers) {
        currentUsers = availableUsers[editableGroup.id] ?: emptyList()

        // Если загружается существующая задача, восстанавливаем исполнителя
        if (task != null && task.assignee != null) {
            val users = availableUsers[editableGroup.id]
            users?.forEach { usr ->
                if (usr.id == task.assignee) {
                    editableAssignee = usr
                }
            }
        } else if (task == null && currentUsers.isNotEmpty() && editableAssignee == defaultUser) {
            // Для новой задачи устанавливаем первого пользователя только если еще не выбран исполнитель
            editableAssignee = currentUsers[0]
        }
    }

    // Обновляем исполнителя только при смене группы (не при обновлении availableUsers)
    LaunchedEffect(editableGroup.id) {
        currentUsers = availableUsers[editableGroup.id] ?: emptyList()
        // Сбрасываем исполнителя только при смене группы
        if (currentUsers.isNotEmpty()) {
            editableAssignee = currentUsers[0]
        } else {
            editableAssignee = defaultUser
        }
    }

    val isNewTask = remember(task, isEditMode) { isEditMode && task == null }
    var isInEditMode by remember { mutableStateOf(isEditMode) }

    LaunchedEffect(task) {
        if (task != null) {
            if (task.groupId == null) {
                editableGroup = defaultGroup
            } else {
                for (gr in availableGroups) {
                    if (gr.id == task.groupId) {
                        editableGroup = gr
                    }
                }
            }

            val users = availableUsers[editableGroup.id]
            users?.forEach { usr ->
                if (usr.id == task.assignee) {
                    editableAssignee = usr
                }
            }

            editableTitle = task.title
            editableDescription = task.description
            editableGeotag = task.geotag?.name ?: ""
            // Инициализируем координаты из существующей задачи
            editableLat = task.geotag?.latitude
            editableLon = task.geotag?.longitude
            // Инициализируем настройки напоминаний
            remindByTime = task.dueDate?.remindByTime ?: false
            remindByLocation = task.geotag?.remindByLocation ?: false
            editableDueDate = task.dueDate?.time ?: kotlin.time.Clock.System.now().toEpochMilliseconds()
            editablePriority = task.priority
            editableStatus = task.status
        }
    }

    // Отслеживание результатов с карты
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

    // Одноразовое чтение результата при возврате с карты
    LaunchedEffect(navController.currentBackStackEntry) {
        val handle = navController.currentBackStackEntry?.savedStateHandle
        val lat: Double? = handle?.get("map_lat")
        val lon: Double? = handle?.get("map_lon")
        val name: String? = handle?.get("map_name")
        if (lat != null && lon != null) {
            editableLat = lat
            editableLon = lon
            editableGeotag = name ?: "${lat.formatLatLon()}, ${lon.formatLatLon()}"

            // Сохраняем новый геотег как пресет
            val geoRepo = AppDependencies.container.geoTagRepository
            val preset = GeoTagPreset(
                name = editableGeotag,
                latitude = lat,
                longitude = lon
            )
            geoRepo.addPreset(preset)

            handle.remove<Double>("map_lat")
            handle.remove<Double>("map_lon")
            handle.remove<String>("map_name")
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            if (isNewTask) "Новая задача"
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
                                val updatedTask = Task(
                                    id = task?.id ?: 0L,
                                    title = editableTitle,
                                    description = editableDescription,
                                    comments = task?.comments ?: emptyList(),
                                    authorId = userId,
                                    groupId = if (editableGroup.id == -1L) null else editableGroup.id,
                                    assignee = editableAssignee.id,
                                    dueDate = Deadline(editableDueDate, remindByTime),
                                    geotag = if (editableLat != null && editableLon != null) {
                                        Location(
                                            editableLat!!,
                                            editableLon!!,
                                            editableGeotag,
                                            remindByLocation
                                        )
                                    } else null,
                                    priority = editablePriority,
                                    status = editableStatus
                                )
                                print("&&&&&&& ")
                                print(updatedTask.groupId)
                                onSave(updatedTask)
                                if (!isNewTask) {
                                    isInEditMode = false
                                } else {
                                    navController.popBackStack()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            enabled = editableTitle.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isNewTask) "Создать" else "Сохранить")
                        }

                        OutlinedButton(
                            onClick = {
                                if (isNewTask) {
                                    navController.popBackStack()
                                } else if (task != null) {
                                    // Reset changes
                                    editableTitle = task.title
                                    editableDescription = task.description
                                    editableGeotag = task.geotag?.name ?: ""
                                    editableLat = task.geotag?.latitude
                                    editableLon = task.geotag?.longitude
                                    editableDueDate = task.dueDate?.time ?: kotlin.time.Clock.System.now().toEpochMilliseconds()
                                    editablePriority = task.priority
                                    editableStatus = task.status

                                    // Сброс группы
                                    if (task.groupId == null) {
                                        editableGroup = defaultGroup
                                    } else {
                                        for (gr in availableGroups) {
                                            if (gr.id == task.groupId) {
                                                editableGroup = gr
                                                break
                                            }
                                        }
                                    }

                                    // Сброс исполнителя
                                    val users = availableUsers[editableGroup.id]
                                    users?.forEach { usr ->
                                        if (usr.id == task.assignee) {
                                            editableAssignee = usr
                                        }
                                    }

                                    // Сброс настроек напоминаний
                                    remindByTime = task.dueDate?.remindByTime ?: false
                                    remindByLocation = task.geotag?.remindByLocation ?: false

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
                                task?.let { onDelete(it) }
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

                // Кнопки тестирования геолокации (только для задач с геонапоминаниями)
                if (!isInEditMode && task?.geotag?.remindByLocation == true) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Простые кнопки тестирования (работают через интерфейс)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val notificationService = AppDependencies.container.notificationService
                                    kotlinx.coroutines.runBlocking {
                                        // Показать тестовое уведомление
                                        notificationService.showInAppNotification(
                                            "🧪 Тест геолокации",
                                            "Эмуляция движения к месту задачи запущена!"
                                        )
                                    }
                                } catch (e: Exception) {
                                    // Игнорируем ошибки
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.secondary
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("🧪 Тест геолокации")
                        }

                        OutlinedButton(
                            onClick = {
                                try {
                                    val notificationService = AppDependencies.container.notificationService
                                    kotlinx.coroutines.runBlocking {
                                        // Показать уведомление об остановке
                                        notificationService.showInAppNotification(
                                            "⏹ Тест остановлен",
                                            "Используйте настройки для полных демо"
                                        )
                                    }
                                } catch (e: Exception) {
                                    // Игнорируем ошибки
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.outline
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text("⏹ Остановить")
                        }
                    }

                    // Описание тестов
                    Text(
                        "Полное тестирование геолокации доступно в разделе 'Настройки'",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Title
            if (isInEditMode) {
                OutlinedTextField(
                    value = editableTitle,
                    onValueChange = { editableTitle = it },
                    label = { Text("Название задачи") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.displayLarge,
                    singleLine = true
                )
            } else {
                Text(editableTitle, style = MaterialTheme.typography.displayLarge)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text("Детали задачи", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(15.dp))

            // Priority
            if (isInEditMode) {
                var priorityExpanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = priorityExpanded,
                        onExpandedChange = { priorityExpanded = !priorityExpanded },
                        modifier = Modifier.clickable { priorityExpanded = true }
                    ) {
                        OutlinedTextField(
                            value = when (editablePriority) {
                                Priority.LOW -> "Низкий"
                                Priority.MIDDLE -> "Средний"
                                Priority.HIGH -> "Высокий"
                            },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            label = { Text("Приоритет") }
                        )

                        ExposedDropdownMenu(
                            expanded = priorityExpanded,
                            onDismissRequest = { priorityExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            Priority.entries.forEach { priorityOption ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (priorityOption) {
                                                Priority.LOW -> "Низкий"
                                                Priority.MIDDLE -> "Средний"
                                                Priority.HIGH -> "Высокий"
                                            }
                                        )
                                    },
                                    onClick = {
                                        editablePriority = priorityOption
//                                      priorityExpanded = false
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
                        "Приоритет: ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Row(modifier = Modifier.weight(1f)) {
                        when (editablePriority) {
                            Priority.LOW -> LowPriority()
                            Priority.MIDDLE -> MediumPriority()
                            Priority.HIGH -> HighPriority()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status
            if (isInEditMode) {
                var statusExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = when (editableStatus) {
                            Status.DONE -> "Завершено"
                            Status.UNDONE -> "Не завершено"
//

                        },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        label = { Text("Статус") }
                    )

                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        Status.entries.forEach { statusOption ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (statusOption) {
                                            Status.DONE -> "Завершено"
                                            Status.UNDONE -> "Не завершено"
//
                                        }
                                    )
                                },
                                onClick = {
                                    editableStatus = statusOption
                                    statusExpanded = false
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
                        "Статус: ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Row(modifier = Modifier.weight(1f)) {
                        when (editableStatus) {
                            Status.UNDONE -> ToDoStatus()
                            Status.DONE -> DoneStatus()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Group
            if (isInEditMode) {
                var groupExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = groupExpanded,
                    onExpandedChange = { groupExpanded = !groupExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = editableGroup.name,
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
                        editableGroup.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Assignee
            if (isInEditMode) {
                var assigneeExpanded by remember { mutableStateOf(false) }


                ExposedDropdownMenuBox(
                    expanded = assigneeExpanded,
                    onExpandedChange = { assigneeExpanded = !assigneeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = editableAssignee.email,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assigneeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        label = { Text("Исполнитель") }
                    )

                    ExposedDropdownMenu(
                        expanded = assigneeExpanded,
                        onDismissRequest = { assigneeExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        currentUsers.forEach {  user ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = user.email,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    editableAssignee = user
                                    assigneeExpanded = false
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
                        "Исполнитель: ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        editableAssignee.email,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Due Date
            if (isInEditMode) {
                var showDatePicker by remember { mutableStateOf(false) }
                var showTimePicker by remember { mutableStateOf(false) }
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = editableDueDate
                )

                // Инициализируем время из текущего дедлайна
                val currentDateTime = kotlin.time.Instant.fromEpochMilliseconds(editableDueDate)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                val timePickerState = rememberTimePickerState(
                    initialHour = currentDateTime.hour,
                    initialMinute = currentDateTime.minute
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = formatDate(editableDueDate),
                            onValueChange = { },
                            label = { Text("Дедлайн (дата и время)") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )

                        // Transparent clickable overlay
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true }
                        )
                    }

                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Выбрать дату и время",
                            tint = MaterialTheme.colorScheme.primary
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
                            state = datePickerState,
                            colors = DatePickerDefaults.colors(
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
                                        val hour = timePickerState.hour
                                        val minute = timePickerState.minute

                                        // Преобразуем выбранную дату в локальную дату
                                        val selectedDate = kotlin.time.Instant.fromEpochMilliseconds(millis)
                                        val localDate = selectedDate.toLocalDateTime(TimeZone.currentSystemDefault()).date

                                        // Создаем новую дату со временем
                                        val dateTime = kotlinx.datetime.LocalDateTime(
                                            localDate.year,
                                            localDate.month,
                                            localDate.day,
                                            hour,
                                            minute
                                        )

                                        // Преобразуем в миллисекунды через Instant, используя системную временную зону
                                        val instant = dateTime.toInstant(TimeZone.currentSystemDefault())
                                        editableDueDate = instant.toEpochMilliseconds()
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

                // Чекбокс для напоминания по времени
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Checkbox(
                        checked = remindByTime,
                        onCheckedChange = { remindByTime = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Напоминание по времени",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (remindByTime) {
                    Text(
                        "Уведомления будут отправлены за 3 дня, 1 день и 1 час до дедлайна",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 40.dp, top = 4.dp)
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Дедлайн (увед): ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            formatDate(editableDueDate),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (task?.dueDate?.remindByTime == true) {
                            Text(
                                "🔔 Напоминание включено",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Geotag
            if (isInEditMode) {
                val geoRepo = remember { AppDependencies.container.geoTagRepository }
                var presets by remember { mutableStateOf<List<GeoTagPreset>>(emptyList()) }
                var presetsExpanded by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    geoRepo.presetsFlow().collect { presets = it }
                }

                Box {
                    OutlinedTextField(
                        value = editableGeotag,
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Чекбокс для напоминания по геопозиции
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Checkbox(
                            checked = remindByLocation,
                            onCheckedChange = { remindByLocation = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Напоминание по геопозиции",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (remindByLocation) {
                        Text(
                            "Уведомление при приближении к месту на 100 метров",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 40.dp, top = 4.dp)
                        )
                    }
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            editableGeotag,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (task?.geotag?.remindByLocation == true) {
                            Text(
                                "🌍 Геонапоминание включено",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
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

            Spacer(Modifier.height(30.dp))

            Text("Описание", style = MaterialTheme.typography.headlineLarge)

            Spacer(Modifier.height(15.dp))

            // Description
            if (isInEditMode) {
                OutlinedTextField(
                    value = editableDescription,
                    onValueChange = { editableDescription = it },
                    label = { Text("Описание задачи") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    maxLines = 10
                )
            } else {
                Text(editableDescription, style = MaterialTheme.typography.bodyMedium)
            }
        }
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
