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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

import org.example.project.data.commonData.Group
import org.example.project.data.commonData.Priority
import org.example.project.data.commonData.Status
import org.example.project.data.commonData.Task
import org.example.project.data.commonData.User
import org.example.project.ui.theme.LightGray
import org.example.project.ui.viewComponents.commonComponents.formatDate
import org.example.project.ui.viewComponents.taskScreenComponents.HighPriority
import org.example.project.ui.viewComponents.taskScreenComponents.LowPriority
import org.example.project.ui.viewComponents.taskScreenComponents.MediumPriority
import org.example.project.ui.viewComponents.taskScreenComponents.ToDoStatus
import org.example.project.ui.viewComponents.taskScreenComponents.InProgressStatus
import org.example.project.ui.viewComponents.taskScreenComponents.DoneStatus

@Serializable
data class DetailTaskScreenArgs(val taskID: String?, val isEditMode: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTaskScreen(
    task: Task?,
    navController: NavController,
    isEditMode: Boolean = false,
    availableGroups: List<Group> = emptyList(),
    availableUsers: List<User> = emptyList(),
    onSave: (Task) -> Unit = {},
    onDelete: (Task) -> Unit = {}
) {
    // Default values for new tasks
    val defaultGroup = remember {
        availableGroups.firstOrNull() ?: Group(
            id = "",
            name = "Без группы",
            description = "",
            ownerId = "",
            memberCount = 0,
            members = mutableListOf(),
            createdAt = "",
            taskCount = 0
        )
    }

    val defaultUser = remember {
        availableUsers.firstOrNull() ?: User(
            id = "",
            email = "Не назначен",
            username = "Не назначен",
            privileges = org.example.project.data.commonData.Privileges.member
        )
    }

    // Editable state variables
    var editableTitle by remember { mutableStateOf("") }
    var editableDescription by remember { mutableStateOf("") }
    var editableGeotag by remember { mutableStateOf("") }
    var editableGroup by remember { mutableStateOf(defaultGroup) }
    var editableAssignee by remember { mutableStateOf(defaultUser) }
    @OptIn(ExperimentalTime::class)
    var editableDueDate by remember { mutableStateOf(kotlin.time.Clock.System.now().toEpochMilliseconds()) }
    var editablePriority by remember { mutableStateOf(Priority.MEDIUM) }
    var editableStatus by remember { mutableStateOf(Status.TODO) }

    val isNewTask = remember(task, isEditMode) { isEditMode && task == null }
    var isInEditMode by remember { mutableStateOf(isEditMode) }

    // Initialize fields when task is loaded
    LaunchedEffect(task) {
        if (task != null) {
            editableTitle = task.title
            editableDescription = task.description
            editableGeotag = task.geotag
            editableGroup = task.group
            editableAssignee = task.assignee
            editableDueDate = task.dueDate
            editablePriority = task.priority
            editableStatus = task.status
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
                                    id = task?.id ?: "",
                                    title = editableTitle,
                                    description = editableDescription,
                                    comments = task?.comments ?: emptyList(),
                                    group = editableGroup,
                                    assignee = editableAssignee,
                                    dueDate = editableDueDate,
                                    geotag = editableGeotag,
                                    priority = editablePriority,
                                    status = editableStatus
                                )
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
                                    editableGeotag = task.geotag
                                    editableGroup = task.group
                                    editableAssignee = task.assignee
                                    editableDueDate = task.dueDate
                                    editablePriority = task.priority
                                    editableStatus = task.status
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

                Box {
                    OutlinedTextField(
                        value = when (editablePriority) {
                            Priority.LOW -> "Низкий"
                            Priority.MEDIUM -> "Средний"
                            Priority.HIGH -> "Высокий"
                        },
                        onValueChange = { },
                        label = { Text("Приоритет") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    // Transparent clickable overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { priorityExpanded = true }
                    )

                    DropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        Priority.entries.forEach { priority ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = when (priority) {
                                            Priority.LOW -> "Низкий"
                                            Priority.MEDIUM -> "Средний"
                                            Priority.HIGH -> "Высокий"
                                        },
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    editablePriority = priority
                                    priorityExpanded = false
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
                            Priority.MEDIUM -> MediumPriority()
                            Priority.HIGH -> HighPriority()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status
            if (isInEditMode) {
                var statusExpanded by remember { mutableStateOf(false) }

                Box {
                    OutlinedTextField(
                        value = when (editableStatus) {
                            Status.TODO -> "ToDo"
                            Status.IN_PROGRESS -> "В процессе"
                            Status.DONE -> "Завершено"
                        },
                        onValueChange = { },
                        label = { Text("Статус") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    // Transparent clickable overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { statusExpanded = true }
                    )

                    DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        Status.entries.forEach { status ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = when (status) {
                                            Status.TODO -> "ToDo"
                                            Status.IN_PROGRESS -> "В процессе"
                                            Status.DONE -> "Завершено"
                                        },
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    editableStatus = status
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
                            Status.TODO -> ToDoStatus()
                            Status.IN_PROGRESS -> InProgressStatus()
                            Status.DONE -> DoneStatus()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Group
            if (isInEditMode) {
                var groupExpanded by remember { mutableStateOf(false) }

                Box {
                    OutlinedTextField(
                        value = editableGroup.name,
                        onValueChange = { },
                        label = { Text("Группа") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    // Transparent clickable overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { groupExpanded = true }
                    )

                    DropdownMenu(
                        expanded = groupExpanded,
                        onDismissRequest = { groupExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        availableGroups.forEach { group ->
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

                Box {
                    OutlinedTextField(
                        value = editableAssignee.email,
                        onValueChange = { },
                        label = { Text("Исполнитель") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    // Transparent clickable overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { assigneeExpanded = true }
                    )

                    DropdownMenu(
                        expanded = assigneeExpanded,
                        onDismissRequest = { assigneeExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        availableUsers.forEach { user ->
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
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = editableDueDate
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = formatDate(editableDueDate),
                            onValueChange = { },
                            label = { Text("Дедлайн") },
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
                            contentDescription = "Выбрать дату",
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
                                    datePickerState.selectedDateMillis?.let {
                                        editableDueDate = it
                                    }
                                    showDatePicker = false
                                }
                            ) {
                                Text("OK")
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
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Дедлайн (увед): ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        formatDate(editableDueDate),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Geotag
            if (isInEditMode) {
                OutlinedTextField(
                    value = editableGeotag,
                    onValueChange = { editableGeotag = it },
                    label = { Text("Геометка") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Геометка (увед): ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        editableGeotag,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
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

            Spacer(Modifier.height(30.dp))

            Text("Комментарии", style = MaterialTheme.typography.headlineLarge)
        }
    }
}