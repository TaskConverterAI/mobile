package org.example.project.ui.screens.groupsScreen.creatingGroupScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import org.example.project.ui.screens.statusToast.StatusToast
import org.example.project.ui.screens.statusToast.StatusType
import org.example.project.ui.screens.statusToast.ToastDuration
import org.example.project.ui.theme.LightGray
import org.example.project.ui.viewComponents.GroupScreenComponents.AdminMembersList
import org.example.project.ui.screens.groupsScreen.creatingGroupScreens.isValidEmailOrUsername

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    navController: NavController,
    viewModel: CreateGroupViewModel
) {
    val state by viewModel.uiState.collectAsState()

    var toastMessage by remember { mutableStateOf<String?>(null) }
    var toastType by remember { mutableStateOf(StatusType.ERROR) }
    var toastKey by remember { mutableStateOf(0) }

    LaunchedEffect(state.emailError) {
        if (state.emailError != null) {
            toastMessage = state.emailError
            toastType = StatusType.ERROR
            toastKey++
            viewModel.clearParticipantsError()
        }
    }

    LaunchedEffect(toastKey) {
        if (toastMessage != null) {
            delay(ToastDuration.LONG.millis)
            toastMessage = null
        }
    }

    toastMessage?.let { message ->
        StatusToast(
            type = toastType,
            message = message,
            duration = ToastDuration.LONG,
            onDismiss = { toastMessage = null }
        )
    }

    Scaffold(
        topBar = { CreateGroupTopBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            GroupForm(state = state, viewModel = viewModel, modifier = Modifier.weight(1f))
            CreateButton(
                enabled = viewModel.canCreateGroup(),
                isLoading = state.isLoading
            ) {
                viewModel.createGroup { navController.popBackStack() }
            }
        }

        if (state.addDialogVisible) {
            AddParticipantDialog(state, viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateGroupTopBar(navController: NavController) {
    Column {
        TopAppBar(
            title = { Text("Назад", color = MaterialTheme.colorScheme.primary) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        HorizontalDivider(thickness = 1.dp, color = LightGray)
    }
}

@Composable
private fun GroupForm(
    state: CreateGroupUiState,
    viewModel: CreateGroupViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(12.dp))
        Text("Создание группы", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(24.dp))

        // Название
        Text("Название", style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            value = state.groupName,
            onValueChange = viewModel::onGroupNameChange,
            placeholder = {
                Text(
                    "Work Group",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            shape = RoundedCornerShape(12.dp),
            isError = !state.isGroupNameCorrect || !state.isGroupNameUnique,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = Color.Gray,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            ),
            trailingIcon = {
                if (state.isCheckingGroupName) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (state.groupName.trim().isNotEmpty() &&
                          state.isGroupNameCorrect &&
                          state.isGroupNameUnique) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Название доступно",
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        )

        // Отображаем ошибку валидации или индикатор проверки
        when {
            state.isCheckingGroupName -> {
                Text(
                    text = "Проверка уникальности названия...",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
            !state.isGroupNameCorrect && state.groupNameErrMsg.isNotEmpty() -> {
                Text(
                    text = state.groupNameErrMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
            !state.isGroupNameUnique -> {
                Text(
                    text = "Группа с таким названием уже существует",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
            state.groupName.trim().isNotEmpty() &&
            state.isGroupNameCorrect &&
            state.isGroupNameUnique -> {
                Text(
                    text = "✓ Название доступно",
                    color = Color.Green,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Описание
        Text("Описание", style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            value = state.description,
            onValueChange = viewModel::onDescriptionChange,
            placeholder = {
                Text(
                    "пара слов о группе",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            },
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            minLines = 4,
            isError = !state.isDescriptionCorrect,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = Color.Gray,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Отображаем ошибку валидации описания
        if (!state.isDescriptionCorrect && state.descriptionErrMsg.isNotEmpty()) {
            Text(
                text = state.descriptionErrMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        AdminMembersList(
            state.participants,
            { id -> viewModel.removeParticipant(id) },
            { viewModel.openAddDialog() })

        // Отображаем ошибку лимита участников
        if (state.participantsErrMsg.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = state.participantsErrMsg,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { viewModel.clearParticipantsError() },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Закрыть ошибку",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Дополнительное пояснение
                    if (state.participantsErrMsg.contains("недоступен") || state.participantsErrMsg.contains("не найден")) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "💡 Проверьте правильность email/username или уберите недоступных пользователей для создания группы",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(52.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text("Создать")
        }
    }
}

@Composable
private fun AddParticipantDialog(state: CreateGroupUiState, viewModel: CreateGroupViewModel) {
    AlertDialog(
        onDismissRequest = viewModel::closeAddDialog,
        confirmButton = {},
        title = { Text("Добавить участника") },
        text = {
            Column {
                // Информация о лимите участников
                Text(
                    text = "Участников: ${state.participants.size}/50",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = state.newEmail,
                    onValueChange = viewModel::onNewEmailChange,
                    placeholder = { Text("user@example.com") },
                    label = { Text("Email или username") },
                    singleLine = true,
                    enabled = state.participants.size < 50,
                    isError = state.emailError != null
                )

                state.emailError?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Предупреждение о достижении лимита
                if (state.participants.size >= 50) {
                    Text(
                        text = "Достигнуто максимальное количество участников",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Информация о валидации
                if (state.emailError == null && state.newEmail.isNotEmpty() && isValidEmailOrUsername(state.newEmail.trim())) {
                    val inputType = if (state.newEmail.contains("@")) "email" else "username"
                    Text(
                        text = "✓ Корректный формат $inputType",
                        color = Color.Green,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = "Примечание: Существование пользователя будет проверено при создании группы",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(
                        onClick = viewModel::addParticipant,
                        enabled = state.participants.size < 50 && state.emailError == null && state.newEmail.trim().isNotEmpty()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "add")
                    }
                    IconButton(onClick = viewModel::closeAddDialog) {
                        Icon(Icons.Default.Close, contentDescription = "cancel")
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}


//@Preview(showBackground = true)
//@Composable
//fun CreateGroupScreenPreview() {
//    val vm = CreateGroupViewModel()
//
//    vm.onGroupNameChange("Work Group")
//    vm.onDescriptionChange("пара слов о группе")
//    vm.onNewEmailChange("test@gmail.com")
//    vm.addParticipant()
//    vm.onNewEmailChange("user2@gmail.com")
//    vm.addParticipant()
//
//    TaskConvertAIAppTheme {
//        CreateGroupScreen(
//            navController = rememberNavController(),
//            viewModel = vm
//        )
//    }
//}
